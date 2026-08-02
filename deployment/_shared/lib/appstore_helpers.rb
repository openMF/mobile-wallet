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
end
