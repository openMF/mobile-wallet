# deployment/android/play-closed/lane.rb
# Promotion-only lane (no build): moves the current internal track release to closed/alpha.
# Mirror of promoteToBeta with track_promote_to: alpha. (2026-06-22)

platform :android do
  desc "Promote internal track to closed testing (alpha) on Google Play"
  lane :promoteToClosed do
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
