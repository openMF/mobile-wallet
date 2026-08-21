# deployment/ios/sync-listing/lane.rb
# Syncs App Store metadata (name, subtitle, keywords, description, screenshots)
# WITHOUT uploading an IPA or submitting for review. Safe to run at any time.
# Invocation: (cd deployment && bundle exec fastlane ios syncListing)
require_relative "../../_shared/lib/appstore_helpers"

platform :ios do
  desc "Sync App Store listing (metadata + screenshots) — no build, no binary upload, no submission"
  lane :syncListing do |options|
    options     = sanitize_options(options)
    ios_config  = FastlaneConfig::IosConfig::BUILD_CONFIG
    appstore_config = FastlaneConfig::IosConfig::APPSTORE_CONFIG

    load_api_key(options)

    UI.message("📋 Syncing App Store listing")
    UI.message("   Metadata:     #{ios_config[:metadata_path]}")
    UI.message("   Screenshots:  #{ios_config[:screenshots_path]}")

    deliver(
      api_key:                              Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
      metadata_path:                        ios_config[:metadata_path],
      screenshots_path:                     ios_config[:screenshots_path],
      app_rating_config_path:               ios_config[:app_rating_config_path],
      skip_binary_upload:                   true,
      skip_submission:                      true,
      skip_app_version_update:              true,
      overwrite_screenshots:                true,
      ignore_language_directory_validation: true,
      run_precheck_before_submit:           false,
      force:                                true,   # skip HTML verification prompt
    )

    # Record the pushed hash so a subsequent deploy lane's drift-checked sync correctly skips.
    record_store_listing_synced("ios", ios_config[:metadata_path])

    UI.success("✅ App Store listing synced (no binary uploaded, not submitted)")
  end
end
