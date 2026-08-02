# deployment/ios/firebase/lane.rb
# Imported by fastlane/Fastfile via `import` directive (AC64).
#
# Phase 2 of `deploy-gha-product-flavors` epic (D5/D9): the two per-flavor
# lanes (`deployProdIpaOnFirebase` + `deployDemoIpaOnFirebase`) collapse into
# ONE parameterized lane, `deployIpaOnFirebase(flavor:, build_type:)`. The
# iOS scheme is derived by CONVENTION from
# `cmp-shared/build/kmp-flavors/variants.json` (`{flavor}{BuildType}` — the
# name of the shipped `.xcscheme`) — no more per-flavor lane body, no
# `"iosApp"` hardcoded default scheme burying the flavor dimension.
#
# The pre-Phase-2 names (`deployProdIpaOnFirebase`, `deployDemoIpaOnFirebase`,
# and the legacy `deploy_on_firebase`) are preserved as thin aliases so
# existing GHA snippets + colleague muscle-memory keep working (AC-5).
#
# Signing (three passwords):
#   KEYCHAIN_PASSWORD     — unlocks macOS login keychain (local) or creates CI keychain
#   MATCH_PASSWORD        — decrypts git-stored certs in the Match repo
#   CERTIFICATES_PASSWORD — alternative p12 import password (Match uses MATCH_PASSWORD
#                           for both encryption and import by default; set this only
#                           if your Match repo was initialised with a separate cert password)
require_relative "../../_shared/lib/firebase_helpers"

platform :ios do
  # ── Parameterized lane (Phase 2 primary path) ─────────────────────────────
  desc "Upload iOS IPA to Firebase App Distribution (parameterized on flavor + build_type)"
  lane :deployIpaOnFirebase do |options|
    options   = sanitize_options(options)
    flavor    = (options[:flavor]     || :prod).to_sym
    build_ty  = (options[:build_type] || :release).to_sym

    variant         = VariantResolver.resolve(flavor: flavor.to_s, build_type: build_ty.to_s)
    firebase_config = FastlaneConfig.get_firebase_config(:ios, flavor)
    ios_config      = FastlaneConfig::IosConfig::BUILD_CONFIG

    # Bump build number from Firebase before building.
    increment_version(
      serviceCredsFile: firebase_config[:serviceCredsFile],
      firebase_app_id:  firebase_config[:appId],
    )

    # SSH config + unlock keychain + ASC API key + Match certificate fetch.
    with_ios_preamble(options)
    setup_ios_keychain(keychain_password: options[:keychain_password] || ENV["KEYCHAIN_PASSWORD"])
    load_api_key(options)
    fetch_certificates_with_match(match_type: "adhoc")

    # The Xcode project uses CODE_SIGN_STYLE=Automatic by default. xcodebuild's
    # archive step reads signing settings directly from the project — it does NOT
    # honour gym's export_method. Switch to Manual + Distribution before archiving
    # so xcodebuild finds the AdHoc profile Match just installed instead of looking
    # for a Development profile and failing.
    #
    # `targets:` receives the Xcode TARGET name (`iosApp`), which is FIXED in the KMP
    # template and independent of the flavor. It is NOT the scheme (`prodRelease`,
    # `demoStaging`, …) that build_app's `scheme:` gets — passing a scheme name here
    # matches no target, so update_code_signing_settings silently no-ops and the
    # archive falls back to hunting a Development profile (the 2026-07-31 archive bug).
    update_code_signing_settings(
      use_automatic_signing: false,
      path:                  ios_config[:project_path],
      team_id:               ios_config[:team_id],
      code_sign_identity:    "Apple Distribution",
      targets:               ["iosApp"],  # Xcode TARGET name (fixed in KMP template), NOT the scheme (prodRelease/…) — a scheme here matches no target → update is a silent no-op → archive hunts a Development profile
      bundle_identifier:     ios_config[:app_identifier],
      profile_name:          "match AdHoc #{ios_config[:app_identifier]}",
    )

    # Xcode `configuration` on the .xcscheme sets Debug/Release/Staging linkage;
    # we canonicalise the build_type into a capitalized configuration name so a
    # buildType named "staging" archives against the Staging xcconfig.
    build_app(
      scheme:           variant.ios_scheme,     # ← from resolver, never hardcoded
      workspace:        ios_config[:workspace],
      configuration:    build_ty.to_s.capitalize,
      output_name:      "iosApp-#{flavor}.ipa",
      output_directory: File.join(DEPLOYMENT_REPO_ROOT, "cmp-ios/build"),
      export_method:    "ad-hoc",
      include_bitcode:  false,
    )

    releaseNotes = generateReleaseNote()

    resolved_groups = FirebaseHelpers.resolve_groups(options, firebase_config)
    dist_params = {
      app:                      firebase_config[:appId],
      service_credentials_file: FirebaseHelpers.service_credentials_path(firebase_config),
      release_notes:            releaseNotes,
    }
    dist_params[:groups] = resolved_groups if resolved_groups && !resolved_groups.to_s.strip.empty?
    firebase_app_distribution(dist_params)
  end

  # ── Thin aliases — zero regression for existing callers (AC-5) ────────────
  desc "Alias: prod release iOS IPA → Firebase (delegates to deployIpaOnFirebase)"
  lane(:deployProdIpaOnFirebase) do |options|
    deployIpaOnFirebase(sanitize_options(options).merge(flavor: :prod, build_type: :release))
  end

  desc "Alias: demo release iOS IPA → Firebase (delegates to deployIpaOnFirebase)"
  lane(:deployDemoIpaOnFirebase) do |options|
    deployIpaOnFirebase(sanitize_options(options).merge(flavor: :demo, build_type: :release))
  end

  # Legacy pre-flavor alias (predates the prod/demo split) — kept because the
  # `.github/workflows/*` snippets referenced it before AC64. Defaults to prod.
  desc "Legacy alias: iOS IPA → Firebase (delegates to deployIpaOnFirebase, defaults flavor: :prod)"
  lane(:deploy_on_firebase) do |options|
    deployIpaOnFirebase(sanitize_options(options).merge(flavor: :prod, build_type: :release))
  end
end
