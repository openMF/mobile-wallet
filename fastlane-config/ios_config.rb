module FastlaneConfig
  module IosConfig
    FIREBASE_CONFIG = {
      firebase_app_id: "1:728434912738:ios:86a7badfaed88b841a1dbb",
      firebase_service_creds_file: "secrets/firebaseAppDistributionServiceCredentialsFile.json",
      firebase_groups: "mifos-mobile-apps"
    }

    BUILD_CONFIG = {
          project_path: "cmp-ios/iosApp.xcodeproj",
          workspace_path: "cmp-ios/iosApp.xcworkspace",
          plist_path: "cmp-ios/iosApp/Info.plist",
          scheme: "iosApp",
          output_name: "iosApp.ipa",
          output_directory: "cmp-ios/build",
          match_git_private_key: "./secrets/match_ci_key",
          match_type: "adhoc",
          app_identifier: "org.mifospay",
          provisioning_profile_name: "match AdHoc org.mifospay",
          git_url: "git@github.com:openMF/ios-provisioning-profile.git",
          git_branch: "mifospay",
          key_id: "7V3JGBANF6",
          issuer_id: "7ab9e361-9603-4c3e-b147-be3b0f816099",
          key_filepath: "./secrets/Auth_key.p8",
          version_number: "1.0.0",
          metadata_path: "./fastlane/metadata",
          app_rating_config_path: "./fastlane/age_rating.json"
        }
  end
end