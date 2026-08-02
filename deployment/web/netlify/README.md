# deployment/web/netlify — Netlify

**Tier:** 2 (opt-in)
**Owning capability:** `web-netlify-publish`
**Output:** Kotlin/JS browser distribution → Netlify

## What this deploys

Web bundle deployed via `netlify-cli`. `netlify.toml` sets the COOP/COEP
response headers + SPA-fallback redirect required for Compose Multiplatform
Wasm features.

## Local deploy

```bash
./gradlew :cmp-web:jsBrowserDistribution
netlify deploy --prod \
  --dir=cmp-web/build/dist/js/productionExecutable \
  --site=$NETLIFY_SITE_ID \
  --auth=$NETLIFY_AUTH_TOKEN
```

## Secrets required

| Alias                  | Env                   |
|------------------------|-----------------------|
| `netlify_auth_token`   | `NETLIFY_AUTH_TOKEN`  |
| `netlify_site_id`      | `NETLIFY_SITE_ID`     |
