# deployment/android/sync-listing/lane.rb
# Syncs title, description, screenshots, and changelogs to Play Store
# WITHOUT uploading an APK or AAB. Safe to run at any time — no binary required.
# Invocation: (cd deployment && bundle exec fastlane android syncListing)

platform :android do
  desc "Sync Play Store listing (metadata + screenshots) — no build, no binary upload"
  lane :syncListing do |options|
    options = sanitize_options(options)

    locale = FastlaneConfig::AndroidConfig::PRIMARY_LOCALE   # fork.properties store.primary.locale (was broken SHARED[:primary_locale])
    metadata_root = File.join(DEPLOYMENT_REPO_ROOT, "deployment/android/metadata")

    UI.message("📋 Syncing Play Store listing for locale: #{locale}")
    UI.message("   Metadata root: #{metadata_root}")

    upload_to_play_store(
      track:              "internal",
      skip_upload_apk:    true,
      skip_upload_aab:    true,
      json_key:           File.join(DEPLOYMENT_REPO_ROOT, BuildSecrets.for.path(:play_service_account)),
      package_name:       FastlaneConfig::ProjectConfig.android_package_name,
      metadata_path:      metadata_root,
      # sync_image_upload:  true is the default — uploads screenshots when present
    )

    UI.success("✅ Play Store listing synced (no binary uploaded)")
  end
end
