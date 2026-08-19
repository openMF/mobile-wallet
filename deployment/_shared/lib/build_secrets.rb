# deployment/_shared/lib/build_secrets.rb
#
# The ONE implementation of the secrets resolver. Reads secrets/LAYOUT.yaml (the
# single source of truth for the secrets layout) and answers, for any secret key:
#   - path(key)          → where the file is / will be (live wins, else sample)
#   - value(key)         → the string value (env-first, file fallback)
#   - exists?(key)       → is it materialized?
#   - materialize!(key)  → decode the env/literal into its on-disk path
#   - application_id     → flavor-aware applicationId (libs.versions.toml#appId + suffix)
#
# Exposed two ways from this one file: a Ruby API (BuildSecrets.for(...)) used by
# config.rb + the Fastlane lanes, and a CLI (the `if __FILE__ == $0` block) used by
# the GitHub Actions pre_fastlane_script via deployment/scripts/build-secrets.
#
# Stdlib-only (yaml/json/fileutils/base64) so it runs on the runner's SYSTEM Ruby,
# BEFORE Fastlane's Bundler is set up (pre_fastlane_script timing).
require "yaml"
require "fileutils"
require "base64"

module BuildSecrets
  # LAYOUT lives at <repo>/secrets/LAYOUT.yaml. This file is at
  # <repo>/deployment/_shared/lib/build_secrets.rb → three dirs up to the repo root.
  REPO_ROOT = File.expand_path("../../..", __dir__)
  LAYOUT    = File.join(REPO_ROOT, "secrets", "LAYOUT.yaml")

  # Product-flavor → applicationId suffix. Mirrors org.convention.AppFlavor.
  FLAVOR_SUFFIX = { "prod" => "", "demo" => ".demo" }.freeze

  def self.for(flavor: :prod, variant: :release)
    Accessor.new(flavor.to_s, variant.to_s)
  end

  class Accessor
    def initialize(flavor, variant)
      @flavor  = flavor
      @variant = variant
      @doc     = YAML.load_file(LAYOUT)
    end

    # Spec for a secret, with any `by_flavor[<flavor>]` override merged on top.
    def spec(key)
      base = @doc.fetch("secrets").fetch(key.to_s) { raise "unknown secret '#{key}'" }
      ov   = base["by_flavor"] && base["by_flavor"][@flavor]
      ov ? base.merge(ov) : base
    end

    # Interpolate {flavor}/{variant} into a relative path.
    def rel(key)
      (spec(key)["rel"] || "").gsub("{flavor}", @flavor).gsub("{variant}", @variant)
    end

    # Candidate on-disk locations, in precedence order. `consume_at` (out-of-tree,
    # e.g. cmp-android/google-services.json) short-circuits the root search.
    def candidate_paths(key)
      s = spec(key)
      if s["consume_at"]
        [s["consume_at"].gsub("{flavor}", @flavor).gsub("{variant}", @variant)]
      elsif s["kind"] == "env_var"
        # env-line secrets live at <root>/_env/<VAR> (matches materialize_dest)
        @doc.fetch("precedence").map { |root| File.join(@doc["roots"].fetch(root), "_env", s.fetch("source_env")) }
      else
        @doc.fetch("precedence").map { |root| File.join(@doc["roots"].fetch(root), rel(key)) }
      end
    end

    # candidate_paths returns REPO-ROOT-RELATIVE strings (roots are `secrets/live`
    # etc.). Existence/read MUST be anchored on REPO_ROOT — NOT the process cwd —
    # or resolution silently changes with the caller's directory: pre_fastlane_script
    # runs at repo root (live found) while config.rb runs from deployment/ (live
    # missed → wrong fall-through to the committed sample). Anchor here; still
    # RETURN the relative string (consumers File.join it with DEPLOYMENT_REPO_ROOT).
    def abs(p)
      File.join(REPO_ROOT, p)
    end

    # Resolved read path: first existing candidate, else the highest-precedence one.
    def path(key)
      candidate_paths(key).find { |p| File.exist?(abs(p)) } || candidate_paths(key).last
    end

    def exists?(key)
      candidate_paths(key).any? { |p| File.exist?(abs(p)) }
    end

    # String value: literal constant → its value; else env var first (CI), then
    # the on-disk file (local fallback, REPO_ROOT-anchored like path()).
    def value(key)
      # An UNKNOWN key resolves to nil — consistent with a missing on-disk file → nil below. `.value`
      # is for OPTIONAL secret VALUES (ENV-first, file-fallback); a key that isn't registered for this
      # project must NOT hard-raise and abort config.rb load. It was doing exactly that: config.rb's
      # `module IosConfig` eagerly resolves `:appstore_key_id` at LOAD, so an Android deploy (which never
      # touches iOS) aborted with `unknown secret 'appstore_key_id'` (2026-08-08). `.path` still raises —
      # a structural path is required. The real consumer (an iOS lane) validates a nil at deploy time.
      s = begin; spec(key); rescue; nil; end
      return nil if s.nil?
      return s["value"] if s["kind"] == "literal"
      env = ENV[s["source_env"].to_s]
      return env unless env.to_s.empty?
      a = abs(path(key))
      File.exist?(a) ? File.read(a).strip : nil
    end

    # Flavor-aware applicationId — feeds the FB-PRE-3 app-id↔package preflight.
    def application_id
      toml = File.join(REPO_ROOT, "gradle", "libs.versions.toml")
      base = File.read(toml)[/^appId\s*=\s*"([^"]+)"/, 1]
      raise "appId not found in #{toml}" if base.nil? || base.empty?
      "#{base}#{FLAVOR_SUFFIX.fetch(@flavor, '')}"
    end

    # Write a secret to its on-disk path. Returns the path, or nil if the source is empty.
    def materialize!(key, from_env: nil)
      s    = spec(key)
      kind = s.fetch("kind")
      dest = materialize_dest(key, s)
      body = materialize_body(key, s, kind, from_env)
      return nil if body.nil? || body.empty?

      new_body = (kind == "file" ? body : "#{body.chomp}\n")

      # NEVER clobber a git-TRACKED destination. A `consume_at` that points at a committed reference
      # (e.g. cmp-android/google-services.json ships a COMPLETE per-flavor reference so the template
      # builds out-of-box) is the build's source of truth. Overwriting it with the vault copy — which
      # may be a SUBSET (missing a flavored applicationId) — silently breaks the flavored build at
      # compile time, far from the `/secrets pull` that caused it (2026-08-09: the vault
      # google-services carried 2 of 6 variants → demoDebug link failure). Materialize only to
      # untracked/gitignored destinations (secrets/live/**, _env/**, local.properties, …). A fork
      # that wants a vault-driven file must `git rm --cached` it first (make it untracked).
      if git_tracked?(dest)
        return dest if File.exist?(abs(dest)) && File.read(abs(dest)) == new_body  # identical — no-op
        warn "  ↳ skip #{key}: #{dest} is git-tracked — keeping the committed reference " \
             "(vault copy differs; not clobbering the build source-of-truth)"
        return nil
      end

      FileUtils.mkdir_p(File.dirname(dest))
      # Files keep their bytes verbatim; line-oriented values get a trailing newline.
      File.write(dest, new_body)
      File.chmod(s["mode"].to_i(8), dest) if s["mode"]
      dest
    end

    # Is `dest` (repo-root-relative) a git-TRACKED file? A committed file is authoritative — the
    # `/secrets pull` materialize must not overwrite it. Fails SAFE to false (allow materialize)
    # when git is unavailable. system(out:/err:) redirection is Ruby 2.6-compatible.
    def git_tracked?(dest)
      rel = dest.sub(%r{\A#{Regexp.escape(REPO_ROOT)}/}, "")
      system("git", "-C", REPO_ROOT, "ls-files", "--error-unmatch", "--", rel,
             out: File::NULL, err: File::NULL)
    end

    # Materialize every secret whose source is present (used by `materialize-all`).
    # `map{}.compact` (not `filter_map`) so this runs on macOS system Ruby 2.6 — the
    # local `/secrets pull` delegates here, and filter_map is Ruby 2.7+.
    def materialize_all!
      @doc.fetch("secrets").keys.map { |k| materialize!(k) }.compact
    end

    # Emit the vault-materialization plan: one row per LAYOUT secret carrying a
    # `vault_alias`, as `vault_alias<TAB>source_env<TAB>b64` where b64=1 for `file`
    # kinds (feed base64 → decode_file reproduces exact bytes incl. PEM trailing
    # newline) and 0 for value/env_var (fed raw). `literal` secrets have no vault
    # source so they never appear. `by_flavor` secrets emit one row per flavor
    # override (each carries its own source_env + vault_alias). This is the ONLY
    # place the framework's `/secrets pull` needs to consult to know what to decrypt
    # and how to hand it back — build_secrets.rb stays the LAYOUT's single SoT.
    def vault_plan
      @doc.fetch("secrets").flat_map do |_key, s|
        kind = s["kind"] || "value"
        b64  = kind == "file" ? 1 : 0
        if kind == "properties"
          # Each assembled key that is vault-backed needs its value handed back as an env var
          # (materialize_body reads ENV[source_env]); `literal` keys carry no vault source.
          (s["keys"] || []).map { |k| k["vault_alias"] && [k["vault_alias"], k["source_env"], 0] }.compact
        elsif (fl = s["by_flavor"])
          fl.values.map { |fv| fv["vault_alias"] && [fv["vault_alias"], fv["source_env"] || s["source_env"], b64] }.compact
        elsif (va = s["vault_alias"])
          [[va, s["source_env"], b64]]
        else
          []
        end
      end
    end

    # Provider format: emit `export VAR='value'` lines for every env-consumed secret (kind env_var /
    # value), each value read via the env-first/file-fallback resolver. A deploy step runs
    # `eval "$(build-secrets export-env)"` instead of sourcing secrets/live/_env/* — so the vault-backed
    # files under secrets/live/ are the single source of truth and env is just one on-demand format.
    # (map/compact, not filter_map, for system Ruby 2.6 — same as materialize_all!.)
    def export_env
      @doc.fetch("secrets").map do |key, s|
        kind = s["kind"]
        next nil unless kind == "env_var" || kind == "value"
        senv = s["source_env"]
        next nil if senv.nil? || senv.empty?
        v = value(key).to_s
        next nil if v.empty?
        "export #{senv}='#{v.gsub("'", "'\\''")}'"
      end.compact
    end

    # JVM/Gradle consumption surface. Emit `key=<repo-relative-path>` for every secret that resolves to
    # an on-disk FILE (file/properties/literal kinds + anything with rel/consume_at) via the SAME
    # `path()` algorithm — so the map is a faithful projection of the resolver, no parallel logic. Gradle
    # reads this map (a generated `gradle/secrets-paths.properties`) at configuration time instead of
    # shelling out to this resolver — a config-time subprocess is Windows-fragile + config-cache-hostile
    # + runs on every build. env_var (env-line) secrets are skipped: Gradle never needs those paths.
    # Paths are repo-root-relative with forward slashes (Java .properties-safe; the consumer File.joins
    # them with the Gradle rootProject dir).
    def export_paths
      @doc.fetch("secrets").keys.map do |key|
        s = spec(key)
        next nil if s["kind"] == "env_var"
        next nil unless s["rel"] || s["consume_at"]
        "#{key}=#{path(key)}"
      end.compact
    end

    # Persist the JVM path map to gradle/secrets-paths.properties (repo-root-anchored, gitignored).
    # Called after a materialize (CI / `/secrets pull`) so a subsequent `./gradlew` release build reads
    # real live paths WITHOUT any subprocess. Returns the written destination.
    def write_paths_map!
      dest = File.join(REPO_ROOT, "gradle", "secrets-paths.properties")
      FileUtils.mkdir_p(File.dirname(dest))
      File.write(dest, export_paths.join("\n") + "\n")
      dest
    end

    private

    def materialize_dest(key, s)
      live = @doc["roots"].fetch("live")
      return s["consume_at"].gsub("{flavor}", @flavor).gsub("{variant}", @variant) if s["consume_at"]
      return File.join(live, "_env", s.fetch("source_env")) if s["kind"] == "env_var"
      File.join(live, rel(key))
    end

    def materialize_body(_key, s, kind, from_env)
      case kind
      when "literal"
        s["value"]
      when "value", "env_var"
        ENV[s["source_env"].to_s]
      when "file"
        raw = ENV[(from_env || s["source_env"]).to_s]
        return nil if raw.to_s.empty?
        decode_file(raw, s["encoding"])
      when "properties"
        # Assemble a KEY=VALUE .properties file at the platform path (single source of truth) from
        # vault-backed keys (value read from ENV[source_env]) + `literal` keys. Materialized to
        # `rel:` (NOT _env), e.g. android/keystores/upload_keystore.properties — the exact file
        # config.rb#get_android_signing_config reads. Heals the "signing creds only in _env" gap.
        (s["keys"] || []).map { |k|
          next nil unless k["name"]
          v = k.key?("literal") ? k["literal"] : ENV[k["source_env"].to_s]
          "#{k['name']}=#{v}"
        }.compact.join("\n")
      else
        raise "unknown kind '#{kind}'"
      end
    end

    # base64 → decode; pem-or-base64 → raw PEM passthrough else decode; plain → verbatim.
    def decode_file(raw, encoding)
      case encoding
      when "pem-or-base64"
        raw.lstrip.start_with?("-----BEGIN") ? raw : Base64.decode64(raw)
      when "base64"
        Base64.decode64(raw)
      else
        raw
      end
    end
  end
end

# ── CLI ─────────────────────────────────────────────────────────────────────
if __FILE__ == $PROGRAM_NAME
  cmd, *rest = ARGV
  # keyless commands (application-id, materialize-all) have no positional <key>;
  # a leading "--flag" means the key was omitted.
  key = (rest.first && !rest.first.start_with?("--")) ? rest.shift : nil
  opts = {}
  rest.each_slice(2) { |flag, val| opts[flag.sub(/\A--/, "")] = val }
  acc = BuildSecrets.for(flavor: (opts["flavor"] || "prod"), variant: (opts["variant"] || "release"))

  case cmd
  when "path"            then puts acc.path(key)
  when "value"           then puts acc.value(key)
  when "application-id"  then puts acc.application_id
  when "exists"          then exit(acc.exists?(key) ? 0 : 1)
  when "materialize"
    dest = acc.materialize!(key, from_env: opts["from-env"])
    warn(dest ? "✓ materialized #{key} → #{dest}" : "· skip #{key} (source empty)")
  when "materialize-all"
    acc.materialize_all!.each { |d| warn "✓ #{d}" }
    warn "✓ #{acc.write_paths_map!} (gradle path map)"
  when "vault-plan"
    acc.vault_plan.each { |a, e, b| puts "#{a}\t#{e}\t#{b}" }
  when "export-env"
    puts acc.export_env
  when "export-paths"
    puts acc.export_paths
  else
    abort "usage: build-secrets {path|value|application-id|exists|materialize|materialize-all|vault-plan|export-env|export-paths} " \
          "[key] [--flavor f] [--variant v] [--from-env VAR]"
  end
end
