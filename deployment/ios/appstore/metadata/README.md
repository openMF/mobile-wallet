# apple/ios/media/ — App Store Connect (ASC) screenshot & preview assets

Fork-owned media SoT for the App Store Connect listing. `syncForkConfig` /
`idea-store-assets-generate` read from these subfolders; do not rename them.

> **Deliver reads FLAT — do not deploy these subfolders as-is.** fastlane `deliver`
> globs `screenshots/<locale>/*.png` and infers each device by image RESOLUTION; it
> does **not** recurse into device subfolders. The per-device folders below are the
> authoring/rendering SoT only. `syncForkConfig.deriveForkMedia()` FLATTENS them into
> the deploy tree as `deployment/ios/appstore/metadata/screenshots/<locale>/<device>-<NN>.png`
> — that flat form is what `deliver` uploads. A nested `<locale>/<device>/` tree in the
> deploy dir = **zero** screenshots uploaded (silent vacuous success).
> See RULE-STORE-DELIVER-FLAT-SCREENSHOTS-001.

## Screenshot subfolders

ASC groups screenshots by display size class, not device model — one
representative device per size is uploaded, ASC scales for the rest of that
class. Taxonomy is the framework SoT `core/registries/STORE_ASSET_SPECS.yaml`
(`asc_ios` block) — `idea-store-assets-generate` reads it directly; keep this
table in sync with that registry.

| Folder | Display class | Required size (px, portrait) | Represents | Required | Count |
|---|---|---|---|---|---|
| `iphone-6.9/` | iPhone 6.9" | 1320×2868 | iPhone 17/16 Pro Max | yes | up to 10 |
| `iphone-6.5/` | iPhone 6.5" | 1284×2778 | iPhone 14 Plus, 11 Pro Max, XS Max | no | up to 10 |
| `iphone-6.3/` | iPhone 6.3" | 1206×2622 | iPhone 16 Pro | no | up to 10 |
| `iphone-6.1/` | iPhone 6.1" | 1179×2556 | iPhone 16, 15 | no | up to 10 |
| `iphone-5.5/` | iPhone 5.5" | 1242×2208 | iPhone 8 Plus (legacy, still accepted) | no | up to 10 |
| `ipad-13/` | iPad 13" | 2064×2752 | iPad Pro 13" (M4) | yes | up to 10 |
| `ipad-11/` | iPad 11" | 1668×2420 | iPad Pro 11", iPad Air 11" | no | up to 10 |
| `appPreviews/` | App preview videos | per-device (matches the screenshot size class for that device) | one `.mov`/`.mp4` per targeted size class | no | up to 3 per size class, 15–30s each |

Landscape variants use the transposed dimensions (e.g. iPhone 6.9"
landscape = 2868×1320). Format: PNG or JPEG, RGB, no alpha channel.
ASC requires at minimum the two `required: true` classes (6.9" and 13") for
new submissions — ASC back-fills smaller classes from the largest uploaded
set if omitted. `iphone-6.7/` and `ipad-12.9/` are retired device classes
(superseded by 6.3"/6.1" and the 13"/11" refresh) and are intentionally not
present here.

## Loose files

| File | Size | Format | Notes |
|---|---|---|---|
| `app_icon.png` (or `../../../icons/` per icon convention) | 1024×1024 px | PNG, no alpha, no rounded corners (ASC applies the mask) | App Store listing icon. |

## Source of truth

App Toolkit is a 100% offline, no-login personal-finance utility — capture
Loan Tracker, EMI Calculator, Bill Reminders, Amortization Schedule, Loan
Comparison, Interest Rates, Country Macro Data, Currency Exchange Rates.
Generate via `/idea-store-assets-generate` rather than manual device capture.
