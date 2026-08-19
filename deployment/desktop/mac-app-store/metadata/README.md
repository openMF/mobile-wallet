# apple/macos/media/ — Mac App Store screenshot & icon assets

Fork-owned media SoT for the Mac App Store listing. `syncForkConfig` /
`idea-store-assets-generate` read from these subfolders; do not rename them.

> **Deliver reads FLAT + one size family.** fastlane `deliver` globs
> `screenshots/<locale>/*.png` (device inferred by RESOLUTION; no subfolder recursion),
> and `mac-1280…mac-2880` are all ONE display type (`APP_DESKTOP`, capped at 10).
> `syncForkConfig.deriveForkMedia()` keeps only the LARGEST size family present and
> FLATTENS it to `deployment/desktop/mac-app-store/metadata/screenshots/<locale>/<device>-<NN>.png`.
> The per-device folders below are the authoring/rendering SoT only.
> See RULE-STORE-DELIVER-FLAT-SCREENSHOTS-001.

## Screenshots — per-device folders (SoT)

Taxonomy is the framework SoT `core/registries/STORE_ASSET_SPECS.yaml`
(`asc_macos` block) — `idea-store-assets-generate` writes into these
per-device folders directly; keep this table in sync with that registry.

| Folder | Size (px) | Aspect | Represents | Required | Count |
|---|---|---|---|---|---|
| `mac-1280/` | 1280×800 | 16:10 | Minimum accepted size / MacBook Air 13" class | yes | up to 10 |
| `mac-1440/` | 1440×900 | 16:10 | Standard MacBook Air class | no | up to 10 |
| `mac-2560/` | 2560×1600 | 16:10 | Retina 13" MacBook Pro/Air class | no | up to 10 |
| `mac-2880/` | 2880×1800 | 16:10 | Retina 15"/16" MacBook Pro class | no | up to 10 |

Format: PNG or JPEG, RGB, no alpha channel. ASC scales within the 16:10
family from the largest uploaded set if smaller sizes are omitted.

## Legacy `screenshots/` folder

`screenshots/` (undivided by device) predates the per-device folders above
and is kept for any manually-captured assets not yet sorted into a
`mac-*/` folder. Prefer the per-device folders — they are what
`idea-store-assets-generate` reads/writes.

## Loose files

| File | Size | Format | Notes |
|---|---|---|---|
| `app_icon.png` (or `../../../icons/` per icon convention) | 1024×1024 px | PNG, no alpha, no rounded corners (ASC applies the mask) | Mac App Store listing icon. |

## Source of truth

App Toolkit is a 100% offline, no-login personal-finance utility — capture
the desktop layout for Loan Tracker, EMI Calculator, Bill Reminders,
Amortization Schedule, Loan Comparison, Interest Rates, Country Macro Data,
Currency Exchange Rates. Generate via `/idea-store-assets-generate` rather
than manual capture.
