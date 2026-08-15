# web/media/ — Open Graph / social share preview assets

Fork-owned media SoT for link-preview cards (Cloudflare-hosted web build).
`syncForkConfig` / `idea-store-assets-generate` read from these subfolders;
do not rename them.

## Open Graph images (`og-images/`)

| File | Size (px) | Notes |
|---|---|---|
| `og-image.png` | 1200×630 | `<meta property="og:image">` — Facebook, LinkedIn, Slack, Discord, WhatsApp link previews. Aspect ratio ~1.91:1. |

## Social preview (`social-preview/`)

| File | Size (px) | Notes |
|---|---|---|
| `twitter-card.png` | 1200×628 | `<meta name="twitter:card" content="summary_large_image">` + `<meta name="twitter:image">` — X/Twitter link preview. |

## PWA icons (`pwa-icons/`)

Taxonomy is the framework SoT `core/registries/STORE_ASSET_SPECS.yaml`
(`web` block) — `idea-store-assets-generate` reads it directly.

| File | Size (px) | Notes |
|---|---|---|
| `pwa-512.png` | 512×512 | Required — `manifest.json` maskable/any icon, install prompt. |
| `pwa-192.png` | 192×192 | Required — `manifest.json` smaller icon size, homescreen shortcut. |

## Screenshots (`screenshots/`)

PWA install-prompt / store-listing screenshots, split by form factor per the
`web` block of `STORE_ASSET_SPECS.yaml`.

| Folder | Size (px) | Orientation | Notes |
|---|---|---|---|
| `screenshots/wide/` | 1920×1080 | landscape | Desktop/browser form factor, up to 8. |
| `screenshots/mobile/` | 1080×1920 | portrait | Mobile/PWA-install form factor, up to 8. |

## Favicon

The favicon is NOT stored here — it lives in `app-profile/icons/` (the
shared cross-platform icon set) and is referenced by the web build's
`<link rel="icon">` tags directly from that location.

## Source of truth

App Toolkit is a 100% offline, no-login personal-finance utility. Generate
via `/idea-store-assets-generate` rather than manual capture.
