# deployment/android/play-internal/lane.rb
# Imported by fastlane/Fastfile via `import` directive (AC64).
#
# Phase 2 of `deploy-gha-product-flavors` epic (D5/D9/AC-12): `deployInternal`
# takes `flavor:` + `build_type:` and drives every variant string through
# `VariantResolver` — the former hardcoded `bundleProd` task and the per-flavor
# `BUILD_PATHS` symbolic constants (now REMOVED entirely) are replaced by
# `variant.bundle_task` + `variant.aab_path`, both derived by
# CONVENTION from `cmp-shared/build/kmp-flavors/variants.json`. Play track
# destination is CONFIG (`FastlaneConfig::AndroidConfig.play_tracks_by_flavor`,
# override via `PLAY_TRACKS_BY_FLAVOR` env or `fork.properties`), never a
# per-flavor equality gate in Ruby (AC-12). Secrets flow via
# `variant.secrets.path(:play_service_account)` — no local secret-path literal.
#
# AC32 fix (Play requires AAB, not APK) is unchanged — we call
# `variant.bundle_task` (an `bundle{Flavor}{BuildType}` task) rather than
# `variant.gradle_task` (an `assemble…` task).

platform :android do
  desc "Deploy Android AAB to Google Play Store (parameterized on flavor + build_type; track from config)"
  lane :deployInternal do |options|
    if File.exist?("#{Dir.pwd}/../deployment/_shared/scripts/keystore-manager.sh")
      sh("bash #{Dir.pwd}/../deployment/_shared/scripts/keystore-manager.sh check")
    end

    options   = sanitize_options(options)
    flavor    = (options[:flavor]     || :prod).to_sym
    build_ty  = (options[:build_type] || :release).to_sym

    variant        = VariantResolver.resolve(flavor: flavor.to_s, build_type: build_ty.to_s)
    signing_config = FastlaneConfig.get_android_signing_config(options)

    # Play track destination is CONFIG, not a Ruby conditional (AC-12).
    # `play_tracks_by_flavor` maps each declared flavor → Play track (or nil
    # for Firebase-only). No per-flavor equality check on this dispatch path;
    # adding / renaming a flavor in the DSL requires only a corresponding
    # entry in this map (env var, fork.properties, or
    # `DEFAULT_PLAY_TRACKS_BY_FLAVOR`).
    tracks_by_flavor = FastlaneConfig::AndroidConfig.play_tracks_by_flavor
    track            = tracks_by_flavor[flavor.to_s]
    unless track
      UI.user_error!(
        "flavor '#{flavor}' has no Play track configured " \
        "(see FastlaneConfig::AndroidConfig.play_tracks_by_flavor / PLAY_TRACKS_BY_FLAVOR env)"
      )
    end

    # --- Track-aware versionCode (Option B, epic multi-platform-release-completion D15, 2026-06-22) ---
    # Query the LIVE Play tracks and set VERSION_CODE_OVERRIDE = global_max + 1 so the upload
    # always clears the live ceiling, regardless of which repo builds. A fork's commit count is
    # below the real app's accumulated live codes, which triggers Play's
    #   "cannot rollout: existing users can't upgrade to the newly added APKs".
    # We query production + beta + internal and take the GLOBAL max (a higher track can carry a
    # higher code than internal). Reuses generateVersion's existing VERSION_CODE_OVERRIDE hook,
    # so generateVersion stays generic. deployInternal is the ONLY lane that builds a fresh AAB;
    # promoteToBeta / promote_to_production reuse the uploaded code and need no change.
    #
    # Secrets: `variant.secrets.path(:play_service_account)` resolves through
    # `BuildSecrets` (live-wins-else-sample); the resolver contains no literal
    # `secrets/` path (D6).
    play_json_key = File.join(DEPLOYMENT_REPO_ROOT, variant.secrets.path(:play_service_account))
    play_pkg      = FastlaneConfig::ProjectConfig.android_package_name
    live_max = %w[production beta internal].flat_map do |t|
      begin
        google_play_track_version_codes(package_name: play_pkg, track: t, json_key: play_json_key)
      rescue => e
        UI.important("⚠️ Play track '#{t}' version-code query failed (#{e.message}); skipping it.")
        []
      end
    end.map(&:to_i).max || 0
    if live_max > 0
      ENV["VERSION_CODE_OVERRIDE"] = (live_max + 1).to_s
      UI.message("📈 Track-aware versionCode: live global max=#{live_max} → override=#{ENV['VERSION_CODE_OVERRIDE']}")
    else
      # New app (empty tracks) or API unreachable — fall back to commit_count × 10 (the legacy
      # headroom scheme from multi-platform-build-and-publish.yaml). Logged loudly, never silent.
      fallback = (sh("git rev-list --count HEAD", log: false).strip.to_i rescue 0) * 10
      ENV["VERSION_CODE_OVERRIDE"] = fallback.to_s if fallback > 0
      UI.important("⚠️ No live Play track codes (new app / API unreachable); fallback versionCode=commit_count×10=#{fallback}.")
    end
    # --- end track-aware versionCode ---

    generateVersion(platform: "playstore")
    releaseNotes = generateReleaseNote()

    # Write release notes into Play Store metadata before upload.
    buildConfigPath = "metadata/android/en-US/changelogs/default.txt"
    FileUtils.mkdir_p(File.dirname(buildConfigPath))
    File.write(buildConfigPath, releaseNotes)

    # AC32 FIX — Play needs AAB, not APK. `variant.bundle_task` = `bundle{Flavor}{BuildType}`.
    # Empty `buildType` because the task name is already fully resolved.
    buildAndSignApp(taskName: variant.bundle_task, buildType: "", **signing_config)

    # Full store-listing sync — EXPLICIT (was relying on supply's defaults). Pushes the complete
    # listing AND all media alongside the AAB: title/short/full description, changelogs, the
    # feature graphic, and phone / 7" / 10" screenshots from metadata_path. (2026-06-22)
    upload_to_play_store(
      track:                   track,                                                # ← CONFIG (AC-12)
      aab:                     File.join(DEPLOYMENT_REPO_ROOT, variant.aab_path),    # ← CONVENTION (AC-3)
      json_key:                play_json_key,                                        # ← DELEGATION (AC-4)
      package_name:            FastlaneConfig::ProjectConfig.android_package_name,
      metadata_path:           File.join(DEPLOYMENT_REPO_ROOT, "deployment/android/metadata"),
      skip_upload_metadata:    false,  # title / short_description / full_description
      skip_upload_changelogs:  false,  # changelogs/<locale>/default.txt
      skip_upload_images:      false,  # featureGraphic / icon / promo graphics
      skip_upload_screenshots: false,  # phone / sevenInch / tenInch screenshots
    )

    # A1 pushes the full listing inline (above) — record its hash so a later promotion (A2/A3/closed)
    # correctly skips the drift-checked re-sync when the listing hasn't changed since this build.
    # (RULE-DEPLOY-LISTING-SYNC-ALL-STATES-001)
    record_store_listing_synced("android", File.join(DEPLOYMENT_REPO_ROOT, "deployment/android/metadata"))

    # Attach the configured Google Group (fork.properties play.testers.<track>.googlegroup) to this Play
    # track so every upload reaches the group — a persistent, idempotent binding. `internal` track uses
    # play.testers.internal.googlegroup; `beta`/`closed` uses play.testers.closed.googlegroup. No-op if blank.
    sync_play_testers(
      package_name: FastlaneConfig::ProjectConfig.android_package_name,
      track:        (track.to_s == "internal" ? "internal" : "closed"),
      json_key:     play_json_key,
    )
  end
end
