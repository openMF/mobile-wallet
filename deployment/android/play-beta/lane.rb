# deployment/android/play-beta/lane.rb
# Extracted from legacy `promoteToBeta` lane in fastlane/Fastfile.
#
# Phase 2 of `deploy-gha-product-flavors` epic: promotion-only lane (no
# gradle build), so there is no `assembleProd`/`bundleProd`-style flavor
# hardcode to remove — the lane was already free of per-flavor equality
# gates and does not touch any per-flavor artifact-path constant. The
# only Phase-2 change is: accept an `options` hash with a `flavor:` key so
# the lane is uniformly parameterized (D5 — "one parameterized lane per
# target"), and route the Play service-account secret through the
# `VariantResolver.resolve(...).secrets` accessor so ALL secret paths flow
# through the same delegation the build lanes use (`BuildSecrets.for`).
# `PlayStoreConnect`'s promote-track API is flavor-neutral by design —
# promoting internal → beta moves whichever AAB was uploaded, regardless of
# which flavor produced it — so no `flavor` dimension is needed downstream.

platform :android do
  desc "Promote internal track → beta on Google Play (flavor-neutral by API; flavor passthrough for secrets)"
  lane :promoteToBeta do |options|
    options  = sanitize_options(options)
    flavor   = (options[:flavor]     || :prod).to_sym
    build_ty = (options[:build_type] || :release).to_sym

    # Resolver call is the SoT for the secrets axis even in promotion-only
    # lanes: `variant.secrets.path(:play_service_account)` delegates to
    # `BuildSecrets.for(flavor:, variant:)`, honouring any per-flavor
    # override that lands in `secrets/LAYOUT.yaml` in the future without
    # touching this file (D6).
    variant = VariantResolver.resolve(flavor: flavor.to_s, build_type: build_ty.to_s)

    upload_to_play_store(
      track:                        "internal",
      track_promote_to:             "beta",
      # "completed" (was "draft") — a draft promote leaves the Open Testing track PAUSED so
      # testers never receive it. completed = the beta release goes live. (2026-06-22 fix)
      track_promote_release_status: "completed",
      json_key:                     File.join(DEPLOYMENT_REPO_ROOT, variant.secrets.path(:play_service_account)),
      package_name:                 FastlaneConfig::ProjectConfig.android_package_name,
      skip_upload_changelogs:       true,
      skip_upload_metadata:         true,
      skip_upload_images:           true,
      skip_upload_screenshots:      true,
    )
  end
end
