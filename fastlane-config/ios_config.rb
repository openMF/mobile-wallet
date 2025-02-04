module FastlaneConfig
  module IosConfig
    FIREBASE_CONFIG = {
      firebase_app_id: "1:728434912738:ios:86a7badfaed88b841a1dbb",
      firebase_service_creds_file: "secrets/firebaseAppDistributionServiceCredentialsFile.json",
      firebase_groups: "mifos-mobile-testers"
    }

    BUILD_CONFIG = {
      project_path: "mifospay-ios/iosApp.xcodeproj",
      scheme: "iosApp",
      output_directory: "mifospay-ios/build"
    }
  end
end