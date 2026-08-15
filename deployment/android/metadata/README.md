# android/media/ — Google Play Store listing graphics

Fork-owned media SoT for the Play Console store listing. `syncForkConfig` /
`idea-store-assets-generate` read from these subfolders; do not rename them.

## Subfolders (screenshots)

| Folder | Device class | Required size | Aspect | Count |
|---|---|---|---|---|
| `phoneScreenshots/` | Phone | 1080×1920 px (or any 9:16 / 16:9 ratio, min 320px, max 3840px per side) | 9:16 portrait (or 16:9 landscape) | 2–8 |
| `sevenInchScreenshots/` | 7" tablet | 1080×1920 px minimum, 9:16 or 16:9 | 9:16 portrait (or 16:9 landscape) | 0–8 (optional but recommended if tablet layout differs) |
| `tenInchScreenshots/` | 10" tablet | 1080×1920 px minimum (or up to 7680×4320), 9:16 or 16:9 | 9:16 portrait (or 16:9 landscape) | 0–8 (optional but recommended if tablet layout differs) |
| `wearScreenshots/` | Wear OS | 384×384 px | 1:1 square | 0–8 (only if a Wear OS target ships) |
| `tvScreenshots/` | Android TV | 1920×1080 px | 16:9 landscape | 0–8 (only if an Android TV target ships) |

File format: PNG or JPEG (24-bit, no alpha). App Toolkit ships phone-only
today (no tablet/wear/TV targets) — `sevenInchScreenshots/`,
`tenInchScreenshots/`, `wearScreenshots/`, `tvScreenshots/` stay empty
(`.gitkeep` only) until those form factors are targeted.

## Loose files (directly under `android/media/`)

| File | Size | Format | Notes |
|---|---|---|---|
| `featureGraphic.png` | 1024×500 px | PNG/JPEG, no alpha | Required. Shown at the top of the Play Store listing. |
| `tvBanner.png` | 1280×720 px | PNG/JPEG, no alpha | Only required if an Android TV target ships. |
| `icon.png` | 512×512 px | 32-bit PNG (with alpha) | Hi-res app icon for the Play Console listing (distinct from the launcher icon in `app-profile/icons/`). |

## Source of truth

App Toolkit is a 100% offline, no-login personal-finance utility — screens
to capture: Loan Tracker list, EMI Calculator, Bill Reminders, Amortization
Schedule, Loan Comparison, Interest Rates, Country Macro Data, Currency
Exchange Rates. Generate via `/idea-store-assets-generate` (renders the
composed HTML deck) rather than manual device capture — see
`docs/guides/**` store-media guidance.
