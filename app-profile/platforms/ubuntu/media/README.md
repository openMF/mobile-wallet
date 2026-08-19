# ubuntu/media/ — Snap Store / Flathub / .deb listing assets

Fork-owned media SoT for the Linux distribution listings (Snap Store,
Flathub, `.deb` packaging metadata). `syncForkConfig` /
`idea-store-assets-generate` read from these subfolders; do not rename them.

## Screenshots (`screenshots/`)

| Store | Min size (px) | Max size (px) | Notes |
|---|---|---|---|
| Snap Store | 480×480 | 3840×2160 | PNG or JPEG; up to 5 recommended |
| Flathub | 640px on the shortest side | no hard max (larger preferred, 16:9 or similar) | PNG; `metainfo.xml` `<screenshots>` block references these |

## Banner (`banner/`)

| File | Size (px) | Notes |
|---|---|---|
| Snap Store banner | 720×240 | Displayed at the top of the Snap Store listing page |

## Icon (`icon/`)

| File | Size / format | Notes |
|---|---|---|
| `icon.png` | 256×256 px, 32-bit PNG (with alpha) | Snap Store + `.deb` icon |
| `icon.svg` | Scalable vector | Preferred by Flathub/AppStream (`metainfo.xml` `<icon type="stock">` or embedded); falls back to the 256×256 PNG if no SVG is supplied |

## Source of truth

App Toolkit is a 100% offline, no-login personal-finance utility. Generate
via `/idea-store-assets-generate` rather than manual capture.
