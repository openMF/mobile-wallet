# deployment/_shared/listing_sync.rb — intrinsic, drift-checked store-listing sync.
#
# WHY: the store listing (title / description / graphics / screenshots) is DERIVED from app-profile/
# (before_all → ./gradlew syncForkConfig materializes deployment/**/metadata). Historically only a
# FRESH build lane pushed that listing to the store (Android A1 deployInternal, iOS/mac deliver) —
# every Play *promotion* (promoteToBeta / promote_to_production / promoteToClosed) was a pure track
# move with skip_upload_metadata/images/screenshots: true, so promoting an already-built binary after
# the listing changed shipped a STALE listing. Relying on /idea-deploy's external drift gate papered
# over it, but only when the deploy went THROUGH the orchestrator — a raw `fastlane` or CI promote
# bypassed it (user-flagged 2026-08-08: "it should sync on all states A1/A2/A3/A4, even on promote").
#
# WHAT: a shared, DRIFT-CHECKED listing sync every store deploy lane calls. It hashes the
# app-profile-derived metadata dir and pushes the listing ONLY when it changed since the last push
# (or was never pushed) — cheap on unchanged repeats, guaranteed-current on change. The drift state is
# a per-machine hash cache (deployment/fastlane/.listing_sync_state.json, gitignored); on CI (no cache)
# it always syncs, which is the safe default. NEVER skips a first/never-synced listing (empty state key).
#
# Imported by BOTH deployment/Fastfile (CI) and deployment/fastlane/Fastfile (local) after config.rb
# so DEPLOYMENT_REPO_ROOT + FastlaneConfig + BuildSecrets are in scope. Top-level defs become private
# methods on the fastlane runner, so calls to actions (upload_to_play_store) dispatch normally when
# invoked from within a lane — same pattern as config.rb's buildAndSignApp/gradle helpers.
require "digest"
require "json"
require "fileutils"

# Absolute path of the per-machine drift-state cache (gitignored — see .gitignore).
def _listing_sync_state_path
  File.join(DEPLOYMENT_REPO_ROOT, "deployment", "fastlane", ".listing_sync_state.json")
end

# Content hash of every file under metadata_path (path + bytes, sorted for determinism). Images are
# included on purpose: a media change SHOULD re-sync (app-profile media is intentional committed
# content). "" when the dir is absent (before_all/syncForkConfig failed, or not an app-profile fork).
def _listing_sync_hash(metadata_path)
  return "" unless File.directory?(metadata_path)
  files = Dir.glob(File.join(metadata_path, "**", "*")).select { |f| File.file?(f) }.sort
  return "" if files.empty?
  h = Digest::SHA256.new
  files.each do |f|
    rel = f.sub(/\A#{Regexp.escape(metadata_path)}\/?/, "")
    h.update(rel); h.update("\0"); h.update(File.binread(f)); h.update("\0")
  end
  h.hexdigest
end

def _listing_sync_state
  p = _listing_sync_state_path
  return {} unless File.exist?(p)
  JSON.parse(File.read(p)) rescue {}
end

# True when the listing differs from what we last pushed for this platform, OR was never pushed
# (platform key absent). False only when we have a recorded hash AND it matches — so a never-synced
# or CI (no-cache) listing ALWAYS syncs. An absent metadata dir returns false (nothing to push).
def store_listing_needs_sync?(platform_key, metadata_path)
  cur = _listing_sync_hash(metadata_path)
  return false if cur.empty?
  _listing_sync_state[platform_key.to_s] != cur
end

# Persist the current metadata hash as "last pushed" for this platform (call AFTER a successful push).
def record_store_listing_synced(platform_key, metadata_path)
  cur = _listing_sync_hash(metadata_path)
  return if cur.empty?
  st = _listing_sync_state
  st[platform_key.to_s] = cur
  FileUtils.mkdir_p(File.dirname(_listing_sync_state_path))
  File.write(_listing_sync_state_path, JSON.pretty_generate(st))
end

# Drift-checked Play Store listing sync (metadata + images + screenshots, NO binary). Called at the
# head of every Android deploy lane — including the promotion lanes that used to skip the listing.
# Honors options[:skip_listing_sync] (the --skip-listing-sync bypass) and options[:force_listing_sync].
def sync_play_listing_if_changed(options = {})
  return if options[:skip_listing_sync]
  md = File.join(DEPLOYMENT_REPO_ROOT, "deployment/android/metadata")
  unless File.directory?(md)
    UI.important("⏭️  No Android metadata at #{md} — skipping listing sync (before_all/syncForkConfig may have failed)")
    return
  end
  unless options[:force_listing_sync] || store_listing_needs_sync?("android", md)
    UI.message("✓ Play Store listing unchanged since last push — skipping listing sync")
    return
  end
  UI.message("🔄 Play Store listing changed (or never pushed) — syncing app-profile → Play before deploy…")
  upload_to_play_store(
    track:           "internal",   # listing fields are app-level in Play; any track's edit updates them
    skip_upload_apk: true,
    skip_upload_aab: true,
    json_key:        File.join(DEPLOYMENT_REPO_ROOT, BuildSecrets.for.path(:play_service_account)),
    package_name:    FastlaneConfig::ProjectConfig.android_package_name,
    metadata_path:   md,
  )
  record_store_listing_synced("android", md)
  UI.success("✅ Play Store listing synced from app-profile")
end
