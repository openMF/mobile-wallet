# deployment/_shared/lib/firebase_helpers.rb
# Shared Firebase App Distribution helpers extracted from legacy fastlane/Fastfile.
# Consumed by:
#   - deployment/android/firebase/lane.rb (prod + demo flavors)
#   - deployment/ios/firebase/lane.rb
#
# Each consumer require_relative-s this file; the module functions are pure
# helpers so they can be mixed into either :android or :ios platform scope.
require_relative "build_secrets"

module FirebaseHelpers
  module_function

  # Resolve canonical tester groups.
  # Precedence:
  #   1. options[:tester_groups] (workflow input)
  #   2. ENV["FIREBASE_GROUPS"] (CI override — historical workaround)
  #   3. firebase_config[:groups] (fastlane-config default)
  def resolve_groups(options, firebase_config)
    options[:tester_groups] ||
      ENV["FIREBASE_GROUPS"] ||
      firebase_config[:groups]
  end

  # Path to the Firebase service-account JSON. Materialized by /secrets pull
  # OR by manual base64-decode step in workflow-snippet.yml.
  def service_credentials_path(firebase_config)
    firebase_config[:serviceCredsFile] ||
      ENV["FIREBASE_SERVICE_ACCOUNT_PATH"] ||
      BuildSecrets.for.path(:firebase_service_account)   # one canonical path (drops the drifted 2nd filename)
  end
end
