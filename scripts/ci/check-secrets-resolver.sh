#!/usr/bin/env bash
# Phase-7 secrets-resolver guard. Enforces that the build-secrets resolver stays the
# single source of truth for the secrets layout. Run in pre-push / CI.
#
#   SR-7  no resurrected drift literals (the bugs Phase 7 fixed) + workflow uses the resolver
#   SR-8  source_env contract — every LAYOUT source_env is an env var the workflow exposes
#   SR-9  sample completeness — every LAYOUT file/value/literal key has a committed sample file
#   SR-10 no real secrets committed under secrets/sample (placeholder-marked files exempt)
#   SR-11 no hardcoded secrets/<platform>/ path in the production workflow
#   SR-12 no hardcoded service-account path/name anywhere (deployment + workflows + scripts) —
#         the class the firebase/play rename exposed; every consumer resolves via build-secrets
#   SR-13 the JVM/Gradle build consumes the resolver-generated map, never a config-time subprocess
set -uo pipefail
cd "$(git rev-parse --show-toplevel 2>/dev/null || echo .)"
fail=0
LAYOUT=secrets/LAYOUT.yaml
WF=.github/workflows/release-multi-platform.yml

# ── SR-7: the drifts Phase 7 fixed must not return; the workflow materializes via resolver ──
# (CODE only — exclude comment lines so the "DRIFT FIX:" doc comment doesn't self-trip.)
if grep -rnE 'SECRETS_DIR, *"play"|"secrets/play/service-account|firebaseAppDistributionServiceCredentialsFile' \
     deployment --include='*.rb' 2>/dev/null | grep -v build_secrets.rb | grep -vE ':[0-9]+:[[:space:]]*#'; then
  echo "❌ SR-7: a fixed drift literal returned (secrets/play or the 2nd firebase filename)"; fail=1
fi
if grep -nE 'base64 -d > secrets/|base64 -d > cmp-android|printf .* > secrets/apple' "$WF" 2>/dev/null; then
  echo "❌ SR-7: workflow has an inline secret-materialization (use build-secrets materialize)"; fail=1
fi

# ── SR-8: contract — every `--from-env VAR` the workflow materializes maps to a LAYOUT
#     source_env (catches a typo'd/renamed env var in a materialize call). Forwarded-to-
#     publish secrets (azure/vercel/ms/mac) are NOT consumer-materialized, so not checked. ──
LAYOUT_ENVS=$(ruby -ryaml -e 'y=YAML.load_file(ARGV[0]); puts y["secrets"].flat_map{|k,s| (s["by_flavor"]||{"_"=>s}).values.map{|v|v["source_env"]}}.compact.uniq' "$LAYOUT")
for e in $(grep -oE '\-\-from-env [A-Z0-9_]+' "$WF" 2>/dev/null | awk '{print $2}' | sort -u); do
  echo "$LAYOUT_ENVS" | grep -qx "$e" || { echo "❌ SR-8: workflow materializes --from-env $e but no LAYOUT secret declares it"; fail=1; }
done

# ── SR-9: sample completeness (by_flavor expanded; env_var + consume_at exempt) ──
ruby -ryaml -e '
  y=YAML.load_file("'"$LAYOUT"'"); root=y["roots"]["sample"]; miss=[]
  y["secrets"].each{|k,s| next if s["kind"]=="env_var" || s["consume_at"]
    rels = s["by_flavor"] ? s["by_flavor"].values.map{|v|v["rel"]} : [s["rel"]]
    rels.compact.each{|r| p=File.join(root,r); miss<<p unless File.exist?(p)} }
  unless miss.empty?; warn "❌ SR-9: missing sample files:\n  #{miss.join("\n  ")}"; exit 1; end
' || fail=1

# ── SR-10: no real secrets in sample/ (placeholder-marked files are exempt) ──
for f in $(grep -rlE 'sk_live_|sk_test_|-----BEGIN .*PRIVATE KEY-----' secrets/sample 2>/dev/null); do
  grep -q "CLAUDE-PLACEHOLDER" "$f" || { echo "❌ SR-10: real-looking secret (no placeholder marker): $f"; fail=1; }
done

# ── SR-11: no hardcoded on-disk secrets/<platform>/ path in the workflow ──
# Every secrets-tree path the workflow uses MUST come from `$BS path <key>` — a
# literal like `secrets/android/keystores/...` silently breaks the moment LAYOUT.yaml
# restructures the tree (live/+sample/). Comment lines (doc) are exempt.
# (single-file grep -n emits "NUM:content" — one colon — so anchor the comment
#  exclusion at ^NUM: , NOT :NUM: like the grep -rn sites above.)
if grep -nE 'secrets/(android|apple|desktop|web|shared)/[A-Za-z0-9_.-]' "$WF" 2>/dev/null \
     | grep -vE '^[0-9]+:[[:space:]]*#'; then
  echo "❌ SR-11: workflow hardcodes a secrets/<platform>/ path — use \"\$BS\" path <key> instead"; fail=1
fi

# ── SR-12: no hardcoded on-disk secret path (any stale flat `secrets/<platform>/…`) or
#     service-account name — ANY consumer must resolve via build-secrets. Broadened from the
#     firebase/play-only literal to catch the WHOLE class the live/+sample/ split exposed:
#     any `secrets/(android|apple|desktop)/…` that is NOT rooted at secrets/live/ or
#     secrets/sample/ (the resolver composes roots.live/roots.sample + LAYOUT rel: paths, so a
#     flat literal silently breaks the moment the tree restructures) + the bare old
#     `service-account.json` filename. Scans the WIDER surface SR-7/SR-11 missed: deployment +
#     .github/workflows + scripts. Exempt: the resolver itself, the path-declaration SoT
#     (LAYOUT.yaml + secrets-needs.yaml + secrets-manifest.yaml — these DECLARE paths, they
#     don't consume), comment lines, and the sample/live trees. Out-of-scope roots
#     (`secrets/shared/secrets.env`, `secrets/web/…`) + deferred `$SECRETS_DIR/<plat>/` are NOT
#     this class — so they don't trip here; add their platforms once they get LAYOUT rows. ──
SR12=$(grep -rnE 'secrets/(android|apple|desktop)/[A-Za-z0-9_.-]|service-account\.json' \
       deployment .github/workflows scripts \
       --include='*.rb' --include='*.sh' --include='*.yml' --include='*.yaml' 2>/dev/null \
     | grep -vE 'build_secrets\.rb|secrets/LAYOUT\.yaml|secrets-needs\.yaml|secrets-manifest\.yaml|/sample/|/live/' \
     | grep -vE ':[0-9]+:[[:space:]]*#')
if [ -n "$SR12" ]; then
  echo "❌ SR-12: hardcoded service-account path/name (use \`build-secrets path <key>\`):"
  echo "$SR12" | sed 's/^/    /'
  fail=1
fi

# ── SR-13: the Gradle build reads the resolver-generated map, NEVER a config-time subprocess ──
#   The old `providers.exec(... build-secrets path <key> ...)` in SecretsPath.kt was Windows-shebang-
#   fragile + configuration-cache-hostile + ran on every build (broke Desktop/Web CI). build-logic now
#   reads gradle/secrets-paths.properties (build_secrets.rb#export-paths — same algorithm, no drift).
#   Lock it: SecretsPath.kt MUST read that map and MUST NOT spawn a subprocess.
SP=build-logic/convention/src/main/kotlin/org/convention/SecretsPath.kt
if [ -f "$SP" ]; then
  grep -q 'secrets-paths.properties' "$SP" \
    || { echo "❌ SR-13: $SP must read gradle/secrets-paths.properties (the resolver map)"; fail=1; }
  if grep -qE 'providers\.exec|ProcessBuilder|commandLine' "$SP"; then
    echo "❌ SR-13: $SP must NOT exec a subprocess at config time — read the resolver map"; fail=1
  fi
fi

[ "$fail" = 0 ] && echo "✅ secrets-resolver guards pass (SR-7/8/9/10/11/12/13)"
exit $fail
