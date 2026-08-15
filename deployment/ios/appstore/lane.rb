# deployment/ios/appstore/lane.rb
# Imported by fastlane/Fastfile via `import` directive (AC64).
# Extracted from legacy `release` lane (iOS platform block).
# Implements the AC10/AC39 Info.plist write-then-restore pattern via
# AppStoreHelpers.with_plist_backup() — the working tree stays clean after
# the lane runs (success OR failure).
#
# Phase 2 of `deploy-gha-product-flavors` epic (D5/D9): `release` accepts
# `flavor:` + `build_type:` options and drives the Xcode scheme through
# `VariantResolver.resolve(...).ios_scheme` instead of the hardcoded
# `ios_config[:scheme]` fallback ("iosApp"). `promoteToAppStore` +
# `uploadAppStore` remain flavor-neutral because they operate on an
# already-built IPA / an existing TestFlight build.
require_relative "../../_shared/lib/appstore_helpers"
require_relative "../../_shared/lib/version_helpers"

platform :ios do
  desc "Promote an existing TestFlight build to App Store review — no rebuild, no re-upload. Mirrors Android's promote_to_production."
  lane :promoteToAppStore do |options|
    options         = sanitize_options(options)
    ios_config      = FastlaneConfig::IosConfig::BUILD_CONFIG
    appstore_config = FastlaneConfig::IosConfig::APPSTORE_CONFIG

    load_api_key(options)

    # Resolve build number: explicit option > latest TestFlight build
    if options[:build_number]
      build_number = options[:build_number].to_s
      app_version  = options[:app_version] || ios_config[:primary_locale]  # caller must supply version when pinning build
      UI.important("📦 Using provided build #{build_number}")
    else
      build_number = latest_tf_build_number_resilient(
        app_identifier: ios_config[:app_identifier],
        api_key:        Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
      ).to_s
      app_version = Actions.lane_context[SharedValues::LATEST_TESTFLIGHT_VERSION]
      UI.important("📦 Latest TestFlight build: #{build_number}  (#{app_version})")
    end

    UI.message("🚀 Submitting build #{build_number} for App Store review...")
    UI.message("   automatic_release: #{appstore_config[:automatic_release]}  (goes live on approval — no manual step)")

    # HEAL 1 — ensure an EDITABLE App Store version exists for this build's version BEFORE deliver, so a
    # promote never aborts with "could not find an editable version" (the case where every App Store
    # version is READY_FOR_SALE and none matches the build's marketing version). Idempotent create.
    AppStoreHelpers.ensure_editable_appstore_version!(ios_config[:app_identifier], app_version)

    # Drift-checked listing sync (parity with Android/mac): re-upload the app-profile-derived
    # metadata + screenshots ONLY when they changed since the last push (or were never pushed) — an
    # unchanged listing skips the re-upload but the build is still submitted. Defaults toward syncing
    # (never-synced / CI → true). (RULE-DEPLOY-LISTING-SYNC-ALL-STATES-001)
    ios_listing_changed = store_listing_needs_sync?("ios", ios_config[:metadata_path])
    UI.message(ios_listing_changed ? "🔄 App Store listing changed — will upload metadata + screenshots" : "✓ App Store listing unchanged — skipping metadata re-upload")

    # HEAL 2 — a promote NEVER changes app-level identity (name/subtitle). Hide those files around
    # deliver so it syncs ONLY version-level listing and never aborts on the globally-unique app-name
    # conflict ("app name already used on a different account"). Restored after (success OR failure).
    AppStoreHelpers.without_app_identity_metadata(ios_config[:metadata_path]) do
      deliver(
        api_key:                              Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
        app_identifier:                       ios_config[:app_identifier],
        app_version:                          app_version,
        build_number:                         build_number,
        # No binary — use the build already on TestFlight
        skip_binary_upload:                   true,
        # Metadata + screenshots (keeps listing in sync) — gated on drift
        skip_metadata:                        !ios_listing_changed,
        skip_screenshots:                     !ios_listing_changed,
        metadata_path:                        ios_config[:metadata_path],
        screenshots_path:                     ios_config[:screenshots_path],
        overwrite_screenshots:                true,
        ignore_language_directory_validation: true,
        skip_app_version_update:              true,
        # Review + release settings
        submit_for_review:                    true,
        automatic_release:                    appstore_config[:automatic_release],
        phased_release:                       appstore_config[:phased_release],
        reject_if_possible:                   appstore_config[:reject_if_possible],
        app_review_information:               appstore_config[:app_review_information].dup,
        submission_information:               appstore_config[:submission_information],
        run_precheck_before_submit:           false,
        force:                                true,
      )
    end

    # Record the listing hash so a later submit skips the metadata re-upload when unchanged.
    record_store_listing_synced("ios", ios_config[:metadata_path]) if ios_listing_changed

    UI.success("✅ Build #{build_number} submitted for App Store review — will auto-release on approval.")
  end

  desc "Upload an already-built IPA to App Store (skips build; use after release build succeeded but deliver failed)"
  lane :uploadAppStore do |options|
    options         = sanitize_options(options)
    ios_config      = FastlaneConfig::IosConfig::BUILD_CONFIG
    appstore_config = FastlaneConfig::IosConfig::APPSTORE_CONFIG

    load_api_key(options)

    ipa_path = options[:ipa] || File.join(DEPLOYMENT_REPO_ROOT, "cmp-ios/build/iosApp.ipa")
    UI.user_error!("IPA not found at #{ipa_path}") unless File.exist?(ipa_path)
    UI.important("📦 Uploading existing IPA: #{ipa_path} (#{File.size(ipa_path) / 1_048_576} MB)")

    releaseNotes = generateReleaseNote()
    locale = ios_config[:primary_locale]
    release_notes_path = File.join(ios_config[:metadata_path], locale, "release_notes.txt")
    FileUtils.mkdir_p(File.dirname(release_notes_path))
    File.write(release_notes_path, releaseNotes)

    # Drift-checked listing sync (parity): default skip_metadata/skip_screenshots to "sync only when
    # the app-profile-derived listing changed since the last push" — an explicit option still wins.
    # (RULE-DEPLOY-LISTING-SYNC-ALL-STATES-001)
    ios_listing_changed = store_listing_needs_sync?("ios", ios_config[:metadata_path])

    deliver(
      api_key:                              Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
      ipa:                                  ipa_path,
      metadata_path:                        ios_config[:metadata_path],
      screenshots_path:                     ios_config[:screenshots_path],
      skip_metadata:                        options.key?(:skip_metadata) ? options[:skip_metadata] : !ios_listing_changed,
      skip_screenshots:                     options.key?(:skip_screenshots) ? options[:skip_screenshots] : !ios_listing_changed,
      skip_binary_upload:                   options[:skip_binary_upload] || false,
      skip_app_version_update:              options.key?(:skip_app_version_update) ? options[:skip_app_version_update] : options[:skip_binary_upload] || false,
      overwrite_screenshots:                true,
      ignore_language_directory_validation: true,
      run_precheck_before_submit:           false,
      submit_for_review:                    options[:submit_for_review] || appstore_config[:submit_for_review],
      automatic_release:                    options[:automatic_release] || appstore_config[:automatic_release],
      phased_release:                       options[:phased_release] || appstore_config[:phased_release],
      reject_if_possible:                   appstore_config[:reject_if_possible],
      force:                                appstore_config[:force],
      submission_information:               appstore_config[:submission_information],
    )

    record_store_listing_synced("ios", ios_config[:metadata_path]) if ios_listing_changed && !(options.key?(:skip_metadata) && options[:skip_metadata])

    UI.success("✅ Successfully uploaded to App Store!")
  end

  desc "Upload iOS application to App Store (parameterized on flavor + build_type; scheme from resolver)"
  lane :release do |options|
    options          = sanitize_options(options)
    flavor    = (options[:flavor]     || :prod).to_sym
    build_ty  = (options[:build_type] || :release).to_sym

    # Convention-derived Xcode scheme (`{flavor}{BuildType}`) — replaces the
    # pre-Phase-2 hardcoded `ios_config[:scheme]` ("iosApp") fallback.
    variant          = VariantResolver.resolve(flavor: flavor.to_s, build_type: build_ty.to_s)
    ios_config       = FastlaneConfig::IosConfig::BUILD_CONFIG
    appstore_config  = FastlaneConfig::IosConfig::APPSTORE_CONFIG

    with_ios_preamble(options)
    setup_ci_if_needed
    load_api_key(options)

    # E6 — assemble the Kotlin `ComposeApp` XCFramework (SwiftPM/XCFramework; the
    # CocoaPods-free replacement for the old pod-install step) before the
    # `iosApp.xcodeproj` archive. Staging → Release slice.
    assemble_ios_xcframework(build_ty.to_s)

    fetch_certificates_with_match(options.merge(match_type: "appstore"))

    update_code_signing_settings(
      use_automatic_signing: false,
      path:                  ios_config[:project_path],
      team_id:               ios_config[:team_id],
      code_sign_identity:    "Apple Distribution",
      targets:               ["iosApp"],  # Xcode TARGET name (fixed in KMP template), NOT the scheme (prodRelease/…) — a scheme here matches no target → update is a silent no-op → archive hunts a Development profile
      bundle_identifier:     ios_config[:app_identifier],
      profile_name:          "match AppStore #{ios_config[:app_identifier]}",
    )

    if options[:version_number] && options[:build_number]
      version             = options[:version_number].to_s
      next_build_number   = options[:build_number].to_i
      UI.important("📱 Using provided version/build: #{version} (#{next_build_number})")
    else
      gradle_version = get_version_from_gradle(sanitize_for_appstore: true)
      latest_build_number = latest_tf_build_number_resilient(
        app_identifier: options[:app_identifier] || ios_config[:app_identifier],
        api_key: Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
      )
      latest_version    = Actions.lane_context[SharedValues::LATEST_TESTFLIGHT_VERSION]
      version           = AppStoreHelpers.bumped_version(options[:version_number] || gradle_version, latest_version)
      next_build_number = latest_build_number + 1
      UI.important("📱 Final App Store version: #{version}")
    end

    increment_version_number(xcodeproj: ios_config[:project_path], version_number: version)
    increment_build_number(xcodeproj: ios_config[:project_path], build_number: next_build_number)

    plist_path = ios_config[:plist_path]
    # AC10/AC39 — write-then-restore so the working tree stays clean.
    AppStoreHelpers.with_plist_backup(plist_path) do
      update_plist(
        plist_path: plist_path,
        block: proc do |plist|
          plist["NSContactsUsageDescription"] = "This app does not access your contacts. This message is required for compliance only."
          plist["NSLocationWhenInUseUsageDescription"] = "This app does not access your location. This message is required for compliance only."
          plist["NSBluetoothAlwaysUsageDescription"] = "This app does not use Bluetooth. This message is required for compliance only."
        end,
      )

      build_ios_project(
        options.merge(
          scheme:                    variant.ios_scheme,
          configuration:             build_ty.to_s.capitalize,
          provisioning_profile_name: ios_config[:provisioning_profile_appstore],
        ),
      )

      releaseNotes = generateReleaseNote()
      locale = ios_config[:primary_locale]
      release_notes_path = File.join(ios_config[:metadata_path], locale, "release_notes.txt")
      FileUtils.mkdir_p(File.dirname(release_notes_path))
      File.write(release_notes_path, releaseNotes)

      deliver(
        api_key: Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
        copyright: "#{Time.now.year} #{FastlaneConfig::ProjectConfig::ORGANIZATION_NAME}",
        metadata_path: ios_config[:metadata_path],
        screenshots_path: ios_config[:screenshots_path],
        skip_metadata: false,
        skip_screenshots: options.key?(:skip_screenshots) ? options[:skip_screenshots] : false,
        skip_binary_upload: options[:skip_binary_upload] || false,
        overwrite_screenshots: true,
        ignore_language_directory_validation: true,
        app_review_information: appstore_config[:app_review_information].dup,
        submit_for_review: options[:submit_for_review] || appstore_config[:submit_for_review],
        automatic_release: options[:automatic_release] || appstore_config[:automatic_release],
        phased_release: options[:phased_release] || appstore_config[:phased_release],
        skip_app_version_update: options[:skip_app_version_update] || appstore_config[:skip_app_version_update],
        reject_if_possible: appstore_config[:reject_if_possible],
        force: appstore_config[:force],
        precheck_include_in_app_purchases: appstore_config[:precheck_include_in_app_purchases],
        run_precheck_before_submit: appstore_config[:run_precheck_before_submit],
        submission_information: appstore_config[:submission_information],
        app_rating_config_path: ios_config[:app_rating_config_path],
      )

      UI.success("✅ Successfully deployed to App Store!")
    end
  end
end
