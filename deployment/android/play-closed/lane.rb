# deployment/android/play-closed/lane.rb
# Promotion-only lane (no build): moves the current internal track release to closed/alpha.
# Mirror of promoteToBeta with track_promote_to: alpha. (2026-06-22)

platform :android do
  desc "Promote internal track to closed testing (alpha) on Google Play"
  lane :promoteToClosed do |options|
    options = sanitize_options(options)

    # Intrinsic listing sync: promotion skips metadata (below), so push the app-profile-derived
    # listing first when it changed since the last push. Drift-checked no-op when unchanged.
    # (RULE-DEPLOY-LISTING-SYNC-ALL-STATES-001)
    sync_play_listing_if_changed(options)

    upload_to_play_store(
      track:                        "internal",
      track_promote_to:             "alpha",
      track_promote_release_status: "completed",
      json_key:                     File.join(DEPLOYMENT_REPO_ROOT, BuildSecrets.for.path(:play_service_account)),
      package_name:                 FastlaneConfig::ProjectConfig.android_package_name,
      skip_upload_changelogs:       true,
      skip_upload_metadata:         true,
      skip_upload_images:           true,
      skip_upload_screenshots:      true,
    )
  end
end
