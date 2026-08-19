# deployment/web/cloudflare-pages — Cloudflare Pages

**Tier:** 2 (opt-in)
**Owning capability:** `web-cloudflare-pages-publish`
**Output:** Kotlin/JS browser distribution → Cloudflare Pages

## Why Cloudflare Pages

The native `_headers` file sets `Cross-Origin-Embedder-Policy: require-corp` +
`Cross-Origin-Opener-Policy: same-origin` so SharedArrayBuffer works
(required for Compose Multiplatform Wasm + Room/SQLite-on-wasm). GitHub Pages
cannot do this — see `../gh-pages/README.md`.

## Local deploy

```bash
# Build
./gradlew :cmp-web:jsBrowserDistribution

# Stage headers + redirects
cp deployment/web/cloudflare-pages/_headers   cmp-web/build/dist/js/productionExecutable/
cp deployment/web/cloudflare-pages/_redirects cmp-web/build/dist/js/productionExecutable/

# Deploy
wrangler pages deploy cmp-web/build/dist/js/productionExecutable --project-name=mifos-x-web
```

## Secrets required

| Alias                          | Env                          |
|--------------------------------|------------------------------|
| `cloudflare_pages_api_token`   | `CLOUDFLARE_PAGES_API_TOKEN` |
| `cloudflare_account_id`        | `CLOUDFLARE_ACCOUNT_ID`      |
