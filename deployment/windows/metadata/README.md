# windows/media/ — Microsoft Store (Partner Center) listing assets

Fork-owned media SoT for the Microsoft Store listing. `syncForkConfig` /
`idea-store-assets-generate` read from these subfolders; do not rename them.

## Screenshots (`screenshots/`)

| Size (px) | Notes |
|---|---|
| 1366×768 | Minimum accepted size |
| up to 3840×2160 | Maximum accepted size (4K) |

Up to 10 screenshots. Format: PNG or JPEG. Aspect ratio must be consistent
across all uploaded screenshots for a given listing. Desktop-flavored
captures of App Toolkit's screens (Loan Tracker, EMI Calculator, Bill
Reminders, Amortization Schedule, Loan Comparison, Interest Rates, Country
Macro Data, Currency Exchange Rates).

## Store logos (`storeLogos/`)

| File pattern | Size (px) | Purpose |
|---|---|---|
| Square 300×300 Logo | 300×300 | Store listing tile (large) |
| Square 150×150 Logo | 150×150 | Store listing tile (medium) |
| Square 71×71 Logo | 71×71 | Store listing tile (small) |
| Square 44×44 Logo | 44×44 | App list / taskbar icon |
| Wide 310×150 Logo | 620×300 (2x) | Store listing tile (wide) |

## Promotional images (`promotional/`)

| Asset | Size (px) | Notes |
|---|---|---|
| Super hero art | 1920×1080 | Featured/editorial placement |
| Poster art | 1080×1920 | Vertical promotional slot |
| Hero image | 846×468 | Store search/category promotional slot |

## Source of truth

Generate via `/idea-store-assets-generate` rather than manual capture.
