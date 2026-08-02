# deployment/_shared/lib/deploy_helpers.rb
#
# Fastlane helper methods shared across all platform/target lanes.
# Imported (not required) by both Fastfiles so methods run in the Fastlane
# runner instance scope and can call built-in Fastlane actions directly.
#
# Consumed by: fastlane/Fastfile, deployment/Fastfile
# Called from: deployment/android/*/lane.rb, deployment/ios/*/lane.rb,
#              deployment/desktop/mac-app-store/lane.rb

require_relative "../project_config"

# ── Common ────────────────────────────────────────────────────────────────────

# Normalise lane options: ensure all keys are symbols; strip blank string values.
def sanitize_options(options)
  return {} if options.nil?
  options.transform_keys(&:to_sym).reject { |_, v| v.is_a?(String) && v.strip.empty? }
rescue StandardError
  options || {}
end

# Build-agnostic release-note generator.
# Uses changelog_from_git_commits (Fastlane's built-in) matching pre-restructure behaviour.
def generateReleaseNote
  changelog_from_git_commits(
    commits_count: 1,
    pretty:        "- %s",
    merge_commit_filtering: "exclude_merges",
  )
rescue StandardError
  "Release #{Time.now.strftime('%Y-%m-%d')}"
end

# ── Android ───────────────────────────────────────────────────────────────────

# Compute versionCode + versionName, write version.properties for Gradle's
# versionFile task, then query the store for the current live version code
# (Firebase for :firebase platform, Play Store for :playstore).
# platform: "firebase" | "playstore"
def generateVersion(platform: "firebase", **opts)
  cfg         = FastlaneConfig::AndroidConfig
  signing     = FastlaneConfig::AndroidConfig.get_signing_config(opts)
  firebase_cfg = FastlaneConfig::AndroidConfig::FIREBASE[:prod]

  gradle(tasks: ["versionFile"])

  version_code = begin
                   File.read("version.properties")
                       .scan(/^VERSION_CODE=(\d+)/).flatten.first.to_i
                 rescue StandardError
                   `git rev-list --count HEAD`.strip.to_i
                 end
  version_name = begin
                   File.read("version.properties")
                       .scan(/^VERSION_NAME=([^\r\n]+)/).flatten.first.to_s.strip
                 rescue StandardError
                   "1.0.0"
                 end

  if platform == "firebase"
    begin
      latest = firebase_app_distribution_get_latest_release(
        app:                  firebase_cfg[:appId],
        service_credentials_file: firebase_cfg[:serviceCredsFile],
      )
      version_code = latest[:buildVersion].to_i + 1 if latest
    rescue StandardError => e
      UI.warning("generateVersion(firebase): #{e.message}")
    end
  elsif platform == "playstore"
    begin
      codes = google_play_track_version_codes(
        package_name: FastlaneConfig::ProjectConfig.android_package_name,
        track:        "internal",
        json_key:     FastlaneConfig::ProjectConfig::ANDROID[:play_store_json_key],
      )
      version_code = (codes.max || 0) + 1
    rescue StandardError => e
      UI.warning("generateVersion(playstore): #{e.message}")
    end
  end

  File.write("version.properties", "VERSION_CODE=#{version_code}\nVERSION_NAME=#{version_name}\n")
  UI.message("📦 version: #{version_name} (#{version_code}) [#{platform}]")
rescue StandardError => e
  UI.warning("generateVersion: #{e.message}")
end

# Invoke Gradle with signing credentials injected as project properties.
# Validates keystore file presence before building.
# taskName: Gradle task (e.g. "assembleProd", "bundleProd")
# buildType: Gradle build type (default "Release")
def buildAndSignApp(taskName:, buildType: "Release", **signing_config)
  store_file = signing_config[:storeFile]
  unless store_file && File.exist?(store_file)
    UI.error("Keystore not found at '#{store_file}' — aborting build")
    raise "Missing keystore: #{store_file}"
  end

  gradle(
    task:       taskName,
    build_type: buildType,
    properties: {
      "android.injected.signing.store.file"     => store_file,
      "android.injected.signing.store.password" => signing_config[:storePassword],
      "android.injected.signing.key.alias"      => signing_config[:keyAlias],
      "android.injected.signing.key.password"   => signing_config[:keyPassword],
    }.compact,
    flags: "--stacktrace",
  )
end

# ── iOS ───────────────────────────────────────────────────────────────────────

# Increment iOS build number from git commit count.
# serviceCredsFile accepted for call-site compatibility (unused here).
def increment_version(serviceCredsFile: nil)
  build_number = `git rev-list --count HEAD`.strip.to_i
  increment_build_number(build_number: build_number)
rescue StandardError => e
  UI.warning("increment_version: #{e.message}")
end

# Standard iOS preamble: generate the KMP podspec/dummy framework, then CocoaPods install.
def with_ios_preamble(_options = {})
  podfile = "cmp-ios/Podfile"
  return unless File.exist?(podfile)
  # KMP + CocoaPods: cmp_shared.podspec (Kotlin cocoapods plugin) raises unless the dummy
  # framework is generated first → pod install fails "run :generateDummyFramework". Run the
  # same Gradle tasks the project's pr-check.yml (line 61) uses. NOT inside the rescue — a
  # missing framework must fail loud, not silently fall through to an "Unable to open
  # Pods-iosApp.release.xcconfig" archive error. (2026-06-22)
  sh("#{DEPLOYMENT_REPO_ROOT}/gradlew -p #{DEPLOYMENT_REPO_ROOT} :cmp-shared:podspec :cmp-shared:generateDummyFramework")
  cocoapods(podfile: podfile, try_repo_update_on_error: true)
end

# Run Fastlane's setup_ci action only when executing inside a CI environment.
def setup_ci_if_needed
  # ALWAYS run setup_ci (throwaway keychain + Match wired to it), so signing never depends on the
  # developer's login keychain — self-sufficient headless/interactive/CI. (fix 2026-07-31)
  setup_ci(force: true)
rescue StandardError => e
  UI.warning("setup_ci_if_needed: #{e.message}")
end

# Load App Store Connect API key into lane context for downstream actions.
# Key source priority: options → ENV → project_config defaults.
def load_api_key(options = {})
  cfg = FastlaneConfig::IosConfig::BUILD_CONFIG
  app_store_connect_api_key(
    key_id:       options[:appstore_key_id]    || ENV["APPSTORE_KEY_ID"]    || cfg[:key_id],
    issuer_id:    options[:appstore_issuer_id] || ENV["APPSTORE_ISSUER_ID"] || cfg[:issuer_id],
    key_filepath: options[:key_filepath]       || cfg[:key_filepath],
    duration:     1200,
    in_house:     false,
  )
end

# Fetch certificates + provisioning profiles from the Match git repo.
# match_type: "appstore" | "adhoc" | "development"
def fetch_certificates_with_match(options = {})
  cfg = FastlaneConfig::IosConfig::BUILD_CONFIG
  match(
    type:            options[:match_type]     || "appstore",
    app_identifier:  options[:app_identifier] || cfg[:app_identifier],
    team_id:         options[:team_id]        || cfg[:team_id],
    git_url:         options[:git_url]        || cfg[:match_git_url],
    git_branch:      options[:git_branch]     || cfg[:match_git_branch],
    git_private_key: ENV["MATCH_GIT_PRIVATE_KEY"] || cfg[:match_git_private_key],
    readonly:        true,
  )
end

# Read the version string (MAJOR.MINOR.PATCH) from version.properties or version.txt.
# sanitize_for_appstore: true strips pre-release/build metadata identifiers.
def get_version_from_gradle(sanitize_for_appstore: false)
  raw = begin
          props = File.read("version.properties")
          props.scan(/^VERSION_NAME=([^\r\n]+)/).flatten.first.to_s.strip
        rescue StandardError
          nil
        end
  raw = begin
          File.read("version.txt").strip
        rescue StandardError
          "1.0.0"
        end if raw.nil? || raw.empty?

  return raw unless sanitize_for_appstore
  # Strip pre-release + build identifiers: "2026.1.1-beta.0+abc" → "2026.1.1"
  raw.split("-").first.split("+").first
rescue StandardError
  "1.0.0"
end

# Build a signed iOS IPA for Firebase ad-hoc distribution.
def build_signed_ios(options = {})
  cfg = FastlaneConfig::IosConfig::BUILD_CONFIG
  match(
    type:            options[:match_type]               || "adhoc",
    app_identifier:  options[:app_identifier]           || cfg[:app_identifier],
    team_id:         options[:team_id]                  || cfg[:team_id],
    git_url:         options[:git_url]                  || cfg[:match_git_url],
    git_branch:      options[:git_branch]               || cfg[:match_git_branch],
    git_private_key: ENV["MATCH_GIT_PRIVATE_KEY"]       || cfg[:match_git_private_key],
    readonly:        true,
  )
  build_ios_app(
    scheme:           cfg[:scheme],
    workspace:        cfg[:workspace_path],
    export_method:    "ad-hoc",
    output_directory: cfg[:output_directory],
    output_name:      cfg[:output_name],
    export_options: {
      provisioningProfiles: {
        cfg[:app_identifier] => options[:provisioning_profile_name] || cfg[:provisioning_profile_name],
      },
    },
  )
end

# Build a signed iOS IPA for App Store / TestFlight distribution.
def build_ios_project(options = {})
  cfg = FastlaneConfig::IosConfig::BUILD_CONFIG
  build_ios_app(
    scheme:           options[:scheme]    || cfg[:scheme],
    workspace:        options[:workspace] || cfg[:workspace_path],
    output_directory: cfg[:output_directory],
    output_name:      cfg[:output_name],
    export_method:    "app-store",
    export_options: {
      provisioningProfiles: {
        cfg[:app_identifier] => options[:provisioning_profile_name] || cfg[:provisioning_profile_appstore],
      },
    },
  )
end
