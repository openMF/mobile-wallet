# deployment/ios/testflight/lane.rb
# Imported by fastlane/Fastfile via `import` directive (AC64).
# Extracted from legacy `beta` lane (iOS platform block).
#
# Phase 2 of `deploy-gha-product-flavors` epic (D5/D9): TestFlight lanes now
# accept `flavor:` + `build_type:` options and derive the Xcode scheme from
# `VariantResolver.resolve(...).ios_scheme` — the pre-Phase-2 hardcoded
# fallback to `ios_config[:scheme]` ("iosApp") is replaced by the
# convention-derived per-variant scheme (`prodRelease`, `demoStaging`, …).
# All aliases + `uploadTestFlight` / `promoteToExternalBeta` are preserved.
require_relative "../../_shared/lib/appstore_helpers"
require_relative "../../_shared/lib/version_helpers"

platform :ios do
  desc "Upload an already-built IPA to TestFlight (skips build; use after beta build succeeded but pilot upload failed)"
  lane :uploadTestFlight do |options|
    options         = sanitize_options(options)
    ios_config      = FastlaneConfig::IosConfig::BUILD_CONFIG
    testflight_config = FastlaneConfig::IosConfig::TESTFLIGHT_CONFIG

    load_api_key(options)

    ipa_path = options[:ipa] || File.join(DEPLOYMENT_REPO_ROOT, "cmp-ios/build/iosApp.ipa")
    UI.user_error!("IPA not found at #{ipa_path}") unless File.exist?(ipa_path)
    UI.important("📦 Uploading existing IPA: #{ipa_path} (#{File.size(ipa_path) / 1_048_576} MB)")

    releaseNotes = generateReleaseNote()
    locale = ios_config[:primary_locale]
    localized_build_info = {
      "default" => { whats_new: releaseNotes },
      locale    => { whats_new: releaseNotes },
    }

    pilot(
      api_key:                              Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
      apple_id:                             ios_config[:apple_id] || "6744892773",
      ipa:                                  ipa_path,
      beta_app_review_info:                 testflight_config[:beta_app_review_info].dup,
      beta_app_feedback_email:              testflight_config[:beta_app_feedback_email],
      beta_app_description:                 testflight_config[:beta_app_description],
      demo_account_required:                testflight_config[:demo_account_required],
      distribute_external:                  testflight_config[:distribute_external],
      notify_external_testers:              testflight_config[:notify_external_testers],
      groups:                               testflight_config[:groups],
      skip_submission:                      testflight_config[:skip_submission],
      skip_waiting_for_build_processing:    testflight_config[:skip_waiting_for_build_processing],
      submit_beta_review:                   testflight_config[:submit_beta_review],
      expire_previous_builds:               testflight_config[:expire_previous_builds],
      reject_build_waiting_for_review:      testflight_config[:reject_build_waiting_for_review],
      wait_processing_interval:             testflight_config[:wait_processing_interval],
      wait_processing_timeout_duration:     testflight_config[:wait_processing_timeout_duration],
      uses_non_exempt_encryption:           testflight_config[:uses_non_exempt_encryption],
      changelog:                            releaseNotes,
      localized_app_info:                   testflight_config[:localized_app_info],
      localized_build_info:                 localized_build_info,
    )

    UI.success("✅ Successfully uploaded to TestFlight!")
  end

  desc "Upload beta build to TestFlight (parameterized on flavor + build_type; scheme from resolver)"
  lane :beta do |options|
    options = sanitize_options(options)
    flavor    = (options[:flavor]     || :prod).to_sym
    build_ty  = (options[:build_type] || :release).to_sym

    # Xcode scheme is derived by CONVENTION from the manifest — no more
    # hardcoded "iosApp" default. The resolved scheme name matches the
    # `.xcscheme` filenames shipped under
    # `cmp-ios/iosApp.xcodeproj/xcshareddata/xcschemes/` (six today:
    # {prod,demo}{Debug,Staging,Release}).
    variant           = VariantResolver.resolve(flavor: flavor.to_s, build_type: build_ty.to_s)
    ios_config        = FastlaneConfig::IosConfig::BUILD_CONFIG
    testflight_config = FastlaneConfig::IosConfig::TESTFLIGHT_CONFIG

    with_ios_preamble(options)
    setup_ci_if_needed
    load_api_key(options)

    # E6 — assemble the Kotlin `ComposeApp` XCFramework (SwiftPM/XCFramework; the
    # CocoaPods-free replacement for the old pod-install step) so the `iosApp.xcodeproj`
    # archive links the framework the app's Package.swift binary target + embed
    # Run-Script phase consume. Staging → Release slice.
    assemble_ios_xcframework(build_ty.to_s)

    # Auto-sync + verify App Store Connect store config BEFORE the ~15-min build:
    # fail fast if the app record is missing, and create/update the TestFlight "Test
    # Information" (BetaAppLocalization) from TESTFLIGHT_CONFIG so the external-beta
    # promotion never fails with `betaAppLocalizations not found`. Fully automatic —
    # the TestFlight parallel to the Play Store listing sync that runs with the AAB.
    ensure_testflight_store_config(app_identifier: ios_config[:app_identifier])

    # Ensure the ACCOUNT-LEVEL internal + external tester groups exist and are populated (from the reusable
    # _org/testflight-testers.yaml). Internal groups are has_access_to_all_builds, so this build reaches
    # internal testers automatically once it finishes processing — no per-build assignment needed. External
    # testers are wired here and distributed later by `promoteToExternalBeta` (Apple beta review). Best-effort.
    sync_testflight_testers(app_identifier: ios_config[:app_identifier])

    fetch_certificates_with_match(options.merge(match_type: "appstore"))

    # Switch project from whatever signing state the previous lane left it in
    # (e.g. Manual+AdHoc from a Firebase deploy) to Manual+AppStore so xcodebuild
    # archives with the AppStore profile Match just installed. Target is the
    # resolved per-variant scheme, not a generic "iosApp".
    update_code_signing_settings(
      use_automatic_signing: false,
      path:                  ios_config[:project_path],
      team_id:               ios_config[:team_id],
      code_sign_identity:    "Apple Distribution",
      targets:               ["iosApp"],  # Xcode TARGET name (fixed in KMP template), NOT the scheme (prodRelease/…) — a scheme here matches no target → update is a silent no-op → archive hunts a Development profile
      bundle_identifier:     ios_config[:app_identifier],
      profile_name:          "match AppStore #{ios_config[:app_identifier]}",
    )

    gradle_version = get_version_from_gradle(sanitize_for_appstore: true)

    latest_build_number = latest_tf_build_number_resilient(
      app_identifier: options[:app_identifier] || ios_config[:app_identifier],
      api_key: Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
    )
    latest_version = Actions.lane_context[SharedValues::LATEST_TESTFLIGHT_VERSION]

    version = AppStoreHelpers.bumped_version(
      options[:version_number] || gradle_version,
      latest_version,
    )
    UI.important("📱 Final App Store version: #{version}")

    increment_version_number(xcodeproj: ios_config[:project_path], version_number: version)
    increment_build_number(xcodeproj: ios_config[:project_path], build_number: latest_build_number + 1)

    build_ios_project(
      options.merge(
        scheme:                    variant.ios_scheme,
        configuration:             build_ty.to_s.capitalize,
        provisioning_profile_name: ios_config[:provisioning_profile_appstore],
      ),
    )

    releaseNotes = generateReleaseNote()
    locale = ios_config[:primary_locale]
    localized_build_info = {
      "default" => { whats_new: releaseNotes },
      locale    => { whats_new: releaseNotes },
    }

    # Stage 1 = INTERNAL TestFlight only. External distribution + Apple's beta review
    # are owned by Stage 2 (`promoteToExternalBeta`). Beta review — and therefore the
    # app-level "Test Information" (BetaAppLocalization: beta_app_description /
    # beta_app_feedback_email / localized_app_info) — is required ONLY for external
    # testing. Passing that app-level metadata here forced Spaceship to update a
    # BetaAppLocalization that doesn't exist yet on a fresh app record, raising
    # `betaAppLocalizations not found for this app`. Internal uploads need none of it:
    # this mirrors the macOS `desktop_testflight` lane, which uploads cleanly because
    # it sets zero app-level beta metadata. Only BUILD-level "What to Test"
    # (localized_build_info) is set — it's created together with the new build.
    # Every TestFlight deploy now distributes to BOTH the internal (team) and external
    # ({org}-mobile-apps) groups AND submits for Apple beta review, with export compliance
    # answered so the build is immediately usable (never stuck "Missing Compliance"). Group
    # NAMES are the org SoT — workspaces/{ws}/_org/company.yaml#org_identity.apple_testflight_
    # {internal,testers}_group, folded into app-profile → resolved by TESTERS[:ios]. The
    # sync_testflight_testers call above already ENSURED both groups exist (creating any missing
    # one). Internal reaches every build automatically (has_access_to_all_builds); external needs
    # this explicit distribution + review submission. External beta review REQUIRES a valid
    # beta_app_review_info.contact_phone (org SoT deploy_contact.phone) — Apple rejects an empty one.
    testers   = FastlaneConfig::IosConfig::TESTERS[:ios]
    tf_groups = [testers[:internal_group], testers[:external_group]]
                  .map { |g| g.to_s.strip }.reject(&:empty?).uniq
    UI.important("👥 Distributing to TestFlight groups: #{tf_groups.join(', ')} — and submitting for beta review")

    pilot(
      api_key:                           Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
      distribute_external:               true,
      notify_external_testers:           true,
      groups:                            tf_groups,
      submit_beta_review:                true,
      skip_submission:                   false,
      skip_waiting_for_build_processing: false,   # MUST wait for processing to submit for review
      wait_processing_interval:          testflight_config[:wait_processing_interval],
      wait_processing_timeout_duration:  testflight_config[:wait_processing_timeout_duration],
      expire_previous_builds:            testflight_config[:expire_previous_builds],
      uses_non_exempt_encryption:        testflight_config[:uses_non_exempt_encryption],
      beta_app_review_info:              testflight_config[:beta_app_review_info]&.dup,
      reject_build_waiting_for_review:   true,
      changelog:                         releaseNotes,
      localized_build_info:              localized_build_info,
    )

    UI.success("✅ Uploaded to TestFlight, distributed to internal + external (#{tf_groups.join(', ')}), and submitted for beta review!")
  end

  desc "Stage 1 → Stage 2 promotion: distribute an already-uploaded TF build to external testers (no rebuild, no re-upload). Triggers Apple's beta review (~24h)."
  lane :promoteToExternalBeta do |options|
    options           = sanitize_options(options)
    ios_config        = FastlaneConfig::IosConfig::BUILD_CONFIG
    testflight_config = FastlaneConfig::IosConfig::TESTFLIGHT_CONFIG

    load_api_key(options)

    # Resolve build: explicit option > latest TF build for this app.
    build_number = options[:build_number]&.to_s || latest_tf_build_number_resilient(
      app_identifier: ios_config[:app_identifier],
      api_key:        Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
    ).to_s

    # External beta review REQUIRES the app-level Test Information — sync it from
    # config first so this promotion never fails for a fresh app record.
    ensure_testflight_store_config(app_identifier: ios_config[:app_identifier])
    # Ensure the external tester groups + testers exist (from the account-level registry) before distributing.
    sync_testflight_testers(app_identifier: ios_config[:app_identifier])

    # External group comes from gradle/fork.properties (apple.testers.external.group), then explicit
    # option, then a sane default.
    fp_external = FastlaneConfig::IosConfig::TESTERS[:ios][:external_group]
    external_groups = options[:groups] ||
                      (fp_external ? [fp_external] : nil) ||
                      testflight_config[:external_groups] ||
                      ["External Beta"]

    UI.important("📦 Promoting TF build #{build_number} → external testers (#{external_groups.join(', ')})")

    # pilot in distribute-only mode — Spaceship updates the build's group
    # assignment + submits for beta review. No IPA upload.
    pilot(
      api_key:                              Actions.lane_context[SharedValues::APP_STORE_CONNECT_API_KEY],
      app_identifier:                       ios_config[:app_identifier],
      build_number:                         build_number,
      distribute_only:                      true,
      distribute_external:                  true,
      notify_external_testers:              true,
      groups:                               external_groups,
      submit_beta_review:                   true,
      reject_build_waiting_for_review:      true,
      changelog:                            options[:changelog] || generateReleaseNote(),
      beta_app_review_info:                 testflight_config[:beta_app_review_info]&.dup,
      uses_non_exempt_encryption:           testflight_config[:uses_non_exempt_encryption],
    )

    UI.success("✅ Build #{build_number} submitted for Apple's beta review — external testers will receive it on approval (~24h).")
  end
end
