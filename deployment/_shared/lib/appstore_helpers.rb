# deployment/_shared/lib/appstore_helpers.rb
# Shared App Store Connect + Match helpers extracted from legacy fastlane/Fastfile.
# Consumed by:
#   - deployment/ios/testflight/lane.rb
#   - deployment/ios/appstore/lane.rb
#   - deployment/desktop/mac-app-store/lane.rb
#
# Each consumer require_relative-s this file; module functions are pure helpers
# usable inside either :ios or :mac platform scope.

module AppStoreHelpers
  module_function

  # Load the ASC API key from options → ENV → fastlane-config defaults.
  # Returns the resolved configuration hash; caller passes to app_store_connect_api_key().
  def asc_api_key_args(options, ios_config)
    {
      key_id:     options[:appstore_key_id]     || ios_config[:key_id],
      issuer_id:  options[:appstore_issuer_id]  || ios_config[:issuer_id],
      key_filepath: options[:key_filepath]      || ios_config[:key_filepath],
      duration:   1200,
    }
  end

  # Bump iOS/macOS version against latest TestFlight/MAS build to guarantee monotonic.
  # Returns the resolved version string. NO-OP when latest_version is nil/empty.
  def bumped_version(gradle_version, latest_version)
    return gradle_version if latest_version.nil? || latest_version.to_s.empty?
    require "rubygems"
    begin
      if Gem::Version.new(gradle_version) <= Gem::Version.new(latest_version)
        parts = latest_version.split(".")
        parts[-1] = (parts[-1].to_i + 1).to_s
        return parts.join(".")
      end
    rescue ArgumentError
      # Fall through — return gradle version unchanged.
    end
    gradle_version
  end

  # Wrap an Info.plist mutation in backup-then-restore so the working tree
  # stays clean after the lane completes (success OR failure).
  # Caller provides the mutator block.
  def with_plist_backup(plist_path)
    backup = "#{plist_path}.bak"
    require "fileutils"
    FileUtils.cp(plist_path, backup) unless File.exist?(backup)
    begin
      yield
    ensure
      FileUtils.mv(backup, plist_path) if File.exist?(backup)
    end
  end

  # Ensure an EDITABLE App Store version exists for this build's version string so `deliver` never
  # aborts with "could not find an editable version". That happens on a promote when EVERY App Store
  # version is READY_FOR_SALE (published) and none matches the TestFlight build's marketing version —
  # a fresh editable version must be created first (App Store requires the version to exist before a
  # build can be attached + submitted). Idempotent: a no-op when the version already exists (editable
  # or not). Uses the global ASC token already configured by app_store_connect_api_key(). iOS only —
  # macOS/tvOS pass their own platform. Never raises: a create failure is logged, deliver still runs.
  def ensure_editable_appstore_version!(app_identifier, version_string, platform: "IOS")
    require "spaceship"
    return if version_string.nil? || version_string.to_s.strip.empty?
    app = Spaceship::ConnectAPI::App.find(app_identifier)
    return FastlaneCore::UI.important("ensure_editable_appstore_version!: app '#{app_identifier}' not found") unless app
    existing = app.get_app_store_versions.find { |v| v.platform == platform && v.version_string == version_string.to_s }
    if existing
      FastlaneCore::UI.message("🆗 App Store version #{version_string} (#{platform}) already exists — #{existing.app_store_state}")
    else
      app.ensure_version!(version_string.to_s, platform: platform)
      FastlaneCore::UI.message("＋ created editable App Store version #{version_string} (#{platform}) — deliver can now attach the build")
    end
  rescue => e
    FastlaneCore::UI.important("ensure_editable_appstore_version!(#{version_string}) note: #{e.message} — deliver will still attempt the submit")
  end

  # A PROMOTE must NEVER change app-level IDENTITY (name / subtitle). App Store names are GLOBALLY
  # unique and set once at registration; re-pushing a taken name aborts the whole submission
  # ("The app name … is already being used on a different account" — e.g. the app-profile brand
  # name vs a differently-suffixed name already registered on the store). Temporarily hide the
  # app-info files (name/subtitle) around the deliver call so it syncs ONLY version-level content
  # (description / keywords / release-notes / screenshots) and leaves the store's registered name
  # untouched. Restores them after (success OR failure) — the derived metadata stays intact.
  def without_app_identity_metadata(metadata_path)
    require "fileutils"
    moved = []
    Dir.glob(File.join(metadata_path, "*", "{name,subtitle}.txt")).each do |f|
      bak = "#{f}.promote-hidden"
      FileUtils.mv(f, bak); moved << [f, bak]
    end
    FastlaneCore::UI.message("🔒 promote: not touching app name/subtitle (#{moved.size} app-identity file(s) held) — store keeps its registered name; only version listing syncs") unless moved.empty?
    yield
  ensure
    (moved || []).each { |orig, bak| FileUtils.mv(bak, orig) if File.exist?(bak) }
  end
end
