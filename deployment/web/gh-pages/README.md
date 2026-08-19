# deployment/web/gh-pages — GitHub Pages

**Tier:** 1 (default-enabled)
**Owning capability:** `github-pages-publish`
**Output:** Kotlin/JS browser distribution → Pages

## What this deploys

Kotlin/JS web app to GitHub Pages via the `actions/deploy-pages@v4` action.

## Limitation: COOP/COEP not supported

GitHub Pages **cannot set custom HTTP response headers**. Compose-Multiplatform
Wasm features that need `SharedArrayBuffer` (Room/SQLite on wasm) will NOT
work. For full Wasm feature parity, use `deployment/web/cloudflare-pages/`,
`netlify/`, or `vercel/`.

## Local deploy

```bash
# Build only (CI uploads via actions/deploy-pages)
bash deployment/web/gh-pages/script.sh

# Push to gh-pages branch (optional local-only path)
LOCAL_GH_PAGES_PUSH=1 bash deployment/web/gh-pages/script.sh
```
