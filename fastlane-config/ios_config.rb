require_relative 'project_config'

module FastlaneConfig
  module IosConfig
    # Firebase Configuration (reads from IOS)
    FIREBASE_CONFIG = {
      firebase_app_id: ProjectConfig::IOS[:firebase][:app_id],
      firebase_service_creds_file: ProjectConfig.firebase_credentials_file,
      firebase_groups: ProjectConfig::IOS[:firebase][:groups]
    }

    # Build Configuration (reads from both IOS and IOS_SHARED)
    BUILD_CONFIG = {
      # App-specific (from IOS)
      app_identifier: ProjectConfig::IOS[:app_identifier],
      project_path: ProjectConfig::IOS[:project_path],
      workspace_path: ProjectConfig::IOS[:workspace_path],
      plist_path: ProjectConfig::IOS[:plist_path],
      scheme: ProjectConfig::IOS[:scheme],
      output_name: ProjectConfig::IOS[:output_name],
      output_directory: ProjectConfig::IOS[:output_directory],
      version_number: ProjectConfig::IOS[:version_number],
      metadata_path: ProjectConfig::IOS[:metadata_path],
      app_rating_config_path: ProjectConfig::IOS[:age_rating_config_path],
      primary_locale: ProjectConfig::IOS[:primary_locale],

      # Shared (from IOS_SHARED)
      team_id: ProjectConfig::IOS_SHARED[:team_id],
      ci_provider: ProjectConfig::IOS_SHARED[:ci_provider],

      # App Store Connect API (from IOS_SHARED)
      key_id: ProjectConfig::IOS_SHARED[:app_store_connect][:key_id],
      issuer_id: ProjectConfig::IOS_SHARED[:app_store_connect][:issuer_id],
      key_filepath: ProjectConfig::IOS_SHARED[:app_store_connect][:key_filepath],

      # Code Signing & Match (from IOS_SHARED)
      match_type: ProjectConfig::IOS_SHARED[:code_signing][:match_type],
      match_git_private_key: ProjectConfig::IOS_SHARED[:code_signing][:match_git_private_key],
      git_url: ProjectConfig::IOS_SHARED[:code_signing][:match_git_url],
      git_branch: ProjectConfig::IOS_SHARED[:code_signing][:match_git_branch],
      provisioning_profile_name: ProjectConfig::IOS_SHARED[:code_signing][:provisioning_profiles][:adhoc],
      provisioning_profile_appstore: ProjectConfig::IOS_SHARED[:code_signing][:provisioning_profiles][:appstore]
    }

    # TestFlight Configuration (from IOS_SHARED)
    TESTFLIGHT_CONFIG = ProjectConfig::IOS_SHARED[:testflight]

    # App Store Configuration (from IOS_SHARED)
    APPSTORE_CONFIG = ProjectConfig::IOS_SHARED[:appstore]
  end
end
