# app-profile/ — fork-owned white-label deployment SoT

This directory is the **single source of truth** for every fork-owned deployment value
(text + media, per platform). It is **fork-owned**: `sync-dirs.sh` NEVER rewrites it
(`app-profile/** → owner: fork` in `customization-surface.yaml`).

**Edit here — nowhere else.** Do not hand-edit `gradle/fork.properties`; that file is now a
generated / fallback build-bridge (Gradle-native compile-time values) derived from this SoT.

## Layout
- `app.yaml` — COMMON, platform-agnostic values (identity, org, legal, keystore DN, store copy, targets).
- `platforms/android/android.yaml` — Play Store + Android Firebase values.
- `platforms/apple/apple.yaml` — Apple-shared signing / Firebase / TestFlight / ASC.
- `platforms/apple/ios/ios.yaml`, `platforms/apple/macos/macos.yaml` — per-Apple-platform overrides.
- `platforms/web/web.yaml`, `platforms/desktop/desktop.yaml` — web + desktop store packaging.
- `platforms/<platform>/media/`, `icons/` — per-platform media assets (screenshots, feature graphics, icons).

## How `deployment/` consumes it
`deployment/_shared/config.rb` defines an `AppProfile` module that lazily loads `app.yaml` +
all `platforms/**/*.yaml`, deep-merges them, and resolves the flat dotted `fork.properties`
namespace via `AppProfile.get(dotted_key)`. `_fork_prop(key)` tries `AppProfile.get(key)` first,
then falls back to `gradle/fork.properties`. Fastlane lanes are unchanged — they still call
`_fork_prop` / `ForkIdentity`. If `app-profile/` is absent, `_fork_prop` behaves exactly as before.

`SyncForkConfigPlugin` (build-logic) reads the same `app.yaml` + `platforms/**/*.yaml` merge on the
Gradle side to regenerate `fork.properties`, the iOS xcconfig, and the store-metadata `.txt` files —
one white-label contract, two consumers (Ruby/Fastlane + Kotlin/Gradle). It also tokenizes the
template-owned deployment files (Cloudflare `wrangler.toml`, MS-Store `Package.appxmanifest`, and the
Android `deployment/android/*/secrets-needs.yaml` keystore-alias prefix) from these values on sync.

## Media convention (canonical, fork-owned)

`app-profile/platforms/<platform>/media/` is the **single canonical location** for every fork-owned
store media asset — phone / tablet screenshots, feature graphic, OG / social images, promo art — one
directory per platform:

- `platforms/android/media/`     — Play Store screenshots + feature graphic
- `platforms/apple/ios/media/`   — App Store (iPhone/iPad) screenshots
- `platforms/apple/macos/media/` — Mac App Store screenshots
- `platforms/web/media/`         — web / OG / social share images
- `platforms/desktop/media/`     — desktop store art

**How it flows:** `/idea-store-assets-generate` renders the composed store deck and drops the finished
assets into these dirs. The Fastlane deploy lanes then read from the SINGLE canonical Play/App-Store
metadata trees under `deployment/<platform>/metadata/` (e.g. `deployment/android/metadata/en-US/images/`,
the `metadata_path` the Play `supply` lanes point at) — never from a second fastlane-default
`deployment/fastlane/metadata/` root (removed as a stale duplicate). Put source media here; the
generator + lanes own the rest.
