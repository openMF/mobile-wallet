module FastlaneConfig
  module AndroidConfig
    STORE_CONFIG = {
      default_store_file: "release_keystore.keystore",
      default_store_password: "Mifospay",
      default_key_alias: "key0",
      default_key_password: "Mifos@123"
    }

    FIREBASE_CONFIG = {
      firebase_prod_app_id: "1:728434912738:android:0490c291986f0a691a1dbb",
      firebase_demo_app_id: "1:728434912738:android:48ccd9153349f31e1a1dbb",
      firebase_service_creds_file: "secrets/firebaseAppDistributionServiceCredentialsFile.json",
      firebase_groups: "mifos-mobile-testers"
    }

    BUILD_PATHS = {
      prod_apk_path: "mifospay-android/build/outputs/apk/prod/release/mifospay-android-prod-release.apk",
      demo_apk_path: "mifospay-android/build/outputs/apk/demo/release/mifospay-android-demo-release.apk",
      prod_aab_path: "mifospay-android/build/outputs/bundle/prodRelease/mifospay-android-prod-release.aab"
    }
  end
end