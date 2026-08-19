# deployment/android/firebase/lane.rb
# Imported by fastlane/Fastfile via `import` directive (AC64).
#
# Phase 2 of `deploy-gha-product-flavors` epic (D5/D9): the historic pair of
# per-flavor lanes (`deployReleaseApkOnFirebase` for prod + `deployDemoApkOnFirebase`
# for demo) collapses into ONE parameterized lane, `deployApkOnFirebase(flavor:,
# build_type:)`, driven by `VariantResolver`. Every variant identity (gradle
# task, APK output path, iOS scheme) is derived by CONVENTION from
# `cmp-shared/build/kmp-flavors/variants.json` — no `[demo, prod]` list, no
# `assembleProd`/`assembleDemo` literal, no per-flavor artifact-path constant
# survives on the primary dispatch path (AC-3). Secrets are delegated to
# `BuildSecrets.for(flavor:)` via the resolver's `.secrets` accessor (AC-4).
#
# The pre-Phase-2 lane names (`deployReleaseApkOnFirebase` / `deployDemoApkOnFirebase`)
# are preserved as THIN aliases so existing GHA workflow snippets, doc lines,
# and manual invocations keep working with zero regression (AC-5).

require_relative "../../_shared/lib/firebase_helpers"

platform :android do
  # ── Parameterized lane (Phase 2 primary path) ─────────────────────────────
  desc "Publish Android APK to Firebase App Distribution (parameterized on flavor + build_type)"
  lane :deployApkOnFirebase do |options|
    # Preserve the AC64 keystore-manager pre-check that both legacy lanes ran.
    if File.exist?("#{Dir.pwd}/../deployment/_shared/scripts/keystore-manager.sh")
      sh("bash #{Dir.pwd}/../deployment/_shared/scripts/keystore-manager.sh check")
    end

    options   = sanitize_options(options)
    flavor    = (options[:flavor]     || :prod).to_sym
    build_ty  = (options[:build_type] || :release).to_sym

    # Resolver is the ONLY place these strings become live values.
    variant = VariantResolver.resolve(flavor: flavor.to_s, build_type: build_ty.to_s)

    signing_config  = FastlaneConfig.get_android_signing_config(options)
    firebase_config = FastlaneConfig.get_firebase_config(:android, flavor)

    generateVersion(platform: "firebase", **firebase_config)
    releaseNotes = generateReleaseNote()

    # Convention-derived gradle task (assemble{Flavor}{BuildType}) → APK output.
    # `buildAndSignApp` concatenates `taskName` + `buildType` to form the
    # Gradle task, so we pass the fully-resolved task name as `taskName` and an
    # empty `buildType` — the concatenation is a no-op and we route through the
    # existing helper without touching its signature (other callers still work).
    buildAndSignApp(
      taskName:  variant.gradle_task,
      buildType: "",
      **signing_config,
    )

    resolved_groups = FirebaseHelpers.resolve_groups(options, firebase_config)
    dist_params = {
      app:                      firebase_config[:appId],
      android_artifact_type:    "APK",
      # Convention-derived per-variant APK path (cmp-android/build/outputs/apk/{flavor}/{buildType}/…).
      # DEPLOYMENT_REPO_ROOT-anchor: the resolver returns repo-relative paths
      # (portable across fork clones); anchor them here for the fastlane action.
      android_artifact_path:    File.join(DEPLOYMENT_REPO_ROOT, variant.apk_path),
      service_credentials_file: FirebaseHelpers.service_credentials_path(firebase_config),
      release_notes:            releaseNotes,
    }
    dist_params[:groups] = resolved_groups if resolved_groups && !resolved_groups.to_s.strip.empty?
    firebase_app_distribution(dist_params)
  end

  # ── Thin aliases — zero regression for existing callers (AC-5) ────────────
  # These preserve the pre-Phase-2 lane names so `fastlane android
  # deployReleaseApkOnFirebase` / `fastlane android deployDemoApkOnFirebase`
  # and every workflow-snippet.yml that references them keep working
  # verbatim. Each alias is a one-liner that merges the flavor / build_type
  # defaults on top of the caller's options hash (caller can still override).
  desc "Alias: prod release APK → Firebase (delegates to deployApkOnFirebase(flavor: :prod, build_type: :release))"
  lane(:deployReleaseApkOnFirebase) do |options|
    deployApkOnFirebase(sanitize_options(options).merge(flavor: :prod, build_type: :release))
  end

  desc "Alias: demo release APK → Firebase (delegates to deployApkOnFirebase(flavor: :demo, build_type: :release))"
  lane(:deployDemoApkOnFirebase) do |options|
    deployApkOnFirebase(sanitize_options(options).merge(flavor: :demo, build_type: :release))
  end
end
