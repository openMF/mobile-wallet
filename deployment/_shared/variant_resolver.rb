# deployment/_shared/variant_resolver.rb
#
# Single derivation point for every `(flavor, build_type) → build-variant identity`
# tuple consumed by the fastlane deployment lanes and the `resolve-variants`
# GHA job. This resolver is the ONLY place variant strings become live values —
# every lane calls `VariantResolver.resolve(flavor:, build_type:)` and consumes
# the returned `Variant` value object; no lane hardcodes `assembleProd`,
# `bundleDemo`, an `apk/prod/…` path, or an iOS scheme name.
#
# Single source of truth chain (epic `deploy-gha-product-flavors`, D8/D9):
#
#   KMPFlavorsConventionPlugin (kmpFlavors {} DSL)
#     └─ ExportKmpFlavorsManifestTask emits cmp-shared/build/kmp-flavors/variants.json
#         └─ THIS resolver reads the manifest at lane runtime
#             └─ derives gradle_task / bundle_task / apk_path / aab_path / ios_scheme
#                 by CONVENTION (no hardcoded [demo | prod] list, no per-flavor map)
#                 └─ delegates the SECRETS axis to BuildSecrets.for(flavor:, variant:)
#                     (this file contains ZERO on-disk secret path literal — D6 hard
#                     constraint; every secret path is returned by the delegated
#                     BuildSecrets accessor, never composed here)
#
# Adding, renaming, or removing a flavor / build-type in the DSL auto-propagates:
# re-run `exportKmpFlavorsManifest`, and every lane sees the new identity on the
# next call. There is no committed catalog to hand-edit, no per-flavor lane
# body, and no per-flavor secret-path string in this file — the delegation to
# `BuildSecrets.for(flavor:)` is the entire secrets contract.
#
# Stdlib-only (json), so this file loads under the same pre-Bundler timing that
# `build_secrets.rb` already tolerates (GitHub Actions `pre_fastlane_script`).

require "json"
require_relative "lib/build_secrets"

module VariantResolver
  # `ExportKmpFlavorsManifestTask` writes the manifest under the module that
  # applies `org.convention.kmp.flavors` — in this template that is `cmp-shared`
  # (via `cmp.feature.convention`). See `ExportKmpFlavorsManifestTask.kt`
  # doc-comment §"Output schema" for the byte-shape this resolver consumes.
  DEFAULT_MANIFEST_PATH = File.join(
    BuildSecrets::REPO_ROOT, "cmp-shared", "build", "kmp-flavors", "variants.json"
  ).freeze

  # Where the resolver reads the manifest from at runtime. `VARIANTS_MANIFEST`
  # env-var override exists so the `resolve-variants` GHA job can stage the
  # manifest to a canonical CI path (`$GITHUB_WORKSPACE/variants.json`) and hand
  # it to fastlane in one step, without requiring a full gradle build ahead of
  # the deploy lane. Default matches the gradle task's `@OutputFile`.
  def self.manifest_path
    ENV["VARIANTS_MANIFEST"] || DEFAULT_MANIFEST_PATH
  end

  # Load + parse the manifest. Small enough that we re-read on every `resolve`
  # call — keeps a repeated call across two lanes in the same fastlane process
  # cache-coherent when the `resolve-variants` job rewrites `variants.json`
  # between stages (edge case; the manifest is a few hundred bytes).
  def self.load_manifest
    path = manifest_path
    ensure_manifest!(path)
    unless File.exist?(path)
      raise <<~ERR
        VariantResolver: manifest not found at #{path}
        Run `./gradlew :cmp-shared:exportKmpFlavorsManifest` (or the
        `resolve-variants` GHA job stages it) before invoking a deploy lane.
        Override the location with the VARIANTS_MANIFEST env var.
      ERR
    end
    JSON.parse(File.read(path))
  end

  # Self-heal the manifest. Deploy lanes run inside a platform build job on its
  # OWN runner — a different runner from the `validate-flavor` job that first
  # emits the manifest — so the gradle `@OutputFile` is not present yet. When the
  # manifest is missing at its DEFAULT location (no VARIANTS_MANIFEST override to
  # a pre-staged path), regenerate it on demand via the same gradle task.
  # Idempotent + fast (a lightweight @DisableCachingByDefault task); the JDK and
  # gradle wrapper are already provisioned because the build job just assembled
  # the app. This removes the need for a manifest-staging step in every tier-3
  # build action — every fastlane platform (Android Firebase/Play, iOS TestFlight/
  # App Store, macOS) resolves its variant without a "manifest not found" failure.
  def self.ensure_manifest!(path)
    return if File.exist?(path)
    return if ENV["VARIANTS_MANIFEST"] # explicit override → caller owns staging
    root = BuildSecrets::REPO_ROOT
    gradlew = File.join(root, Gem.win_platform? ? "gradlew.bat" : "gradlew")
    return unless File.exist?(gradlew)
    Dir.chdir(root) do
      system(gradlew, ":cmp-shared:exportKmpFlavorsManifest", "-q")
    end
  end

  # Resolve `(flavor, build_type)` into a fully-derived `Variant` value object.
  # All string derivation lives here — every downstream string (`assembleProd`,
  # `bundle/demoRelease/…`, `demoStaging` iOS scheme) is a pure function of the
  # `(flavor, build_type)` pair + the AGP/xcodebuild naming convention. There is
  # NO `[demo | prod]` literal enumeration, NO `case flavor` branch, and NO
  # per-flavor equality gate anywhere in this function — the manifest
  # (Phase 1 SoT) is the source of valid names, and every field below derives
  # by convention.
  def self.resolve(flavor:, build_type: "release")
    manifest   = load_manifest
    f          = flavor.to_s
    bt         = build_type.to_s

    valid_flavors     = manifest.fetch("flavors").map    { |entry| entry.fetch("name") }
    valid_build_types = manifest.fetch("buildTypes").map { |entry| entry.fetch("name") }

    unless valid_flavors.include?(f)
      raise "VariantResolver: unknown flavor '#{f}' — valid: #{valid_flavors.join(', ')}"
    end
    unless valid_build_types.include?(bt)
      raise "VariantResolver: unknown build_type '#{bt}' — valid: #{valid_build_types.join(', ')}"
    end

    cap_f  = f.sub(/\A./, &:upcase)
    cap_bt = bt.sub(/\A./, &:upcase)

    # Convention derivations (D9 anchor). These mirror AGP + xcodebuild naming
    # rules exactly, so the strings this resolver produces line up 1:1 with the
    # gradle tasks the convention plugin registers and the Xcode schemes the
    # `cmp-ios/iosApp.xcodeproj/xcshareddata/xcschemes/` directory ships.
    #
    #   * gradle_task  — `assemble{Flavor}{BuildType}` → APK output
    #   * bundle_task  — `bundle{Flavor}{BuildType}`   → AAB output (Play Store)
    #   * apk_path     — AGP convention:
    #                    `<module>/build/outputs/apk/<flavor>/<buildType>/…`
    #   * aab_path     — AGP convention:
    #                    `<module>/build/outputs/bundle/<flavor><BuildType>/…`
    #   * ios_scheme   — `{flavor}{BuildType}` → shipped Xcode scheme name
    #                    (`prodRelease.xcscheme`, `demoStaging.xcscheme`, …)
    Variant.new(
      flavor:      f,
      build_type:  bt,
      gradle_task: "assemble#{cap_f}#{cap_bt}",
      bundle_task: "bundle#{cap_f}#{cap_bt}",
      apk_path:    "cmp-android/build/outputs/apk/#{f}/#{bt}/cmp-android-#{f}-#{bt}.apk",
      aab_path:    "cmp-android/build/outputs/bundle/#{f}#{cap_bt}/cmp-android-#{f}-#{bt}.aab",
      ios_scheme:  "#{f}#{cap_bt}",
      # Secrets axis is DELEGATED to `BuildSecrets.for(flavor:, variant:)`.
      # This resolver holds NO literal secret path — `BuildSecrets` reads the
      # secrets LAYOUT manifest (its own SoT), applies any per-flavor
      # overrides declared there, and hands back an `Accessor` whose
      # `.path(:alias)` / `.value(:alias)` answer per-flavor. D6: one secrets
      # SoT, no reinvention here.
      secrets: BuildSecrets.for(flavor: f, variant: bt),
    )
  end

  # Value object returned by `.resolve`. `Struct` (not a plain Hash) so callers
  # get readable field accessors (`variant.apk_path`) and immutability by
  # discipline — every field is derived at resolve time; do not mutate.
  Variant = Struct.new(
    :flavor, :build_type,
    :gradle_task, :bundle_task,
    :apk_path, :aab_path,
    :ios_scheme,
    :secrets,
    keyword_init: true,
  )
end
