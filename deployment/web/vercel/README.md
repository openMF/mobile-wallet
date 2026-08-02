# deployment/web/vercel — Vercel

**Tier:** 2 (opt-in)
**Owning capability:** `web-vercel-publish`
**Output:** Kotlin/JS browser distribution → Vercel

## What this deploys

Web bundle to Vercel via `vercel deploy`. `vercel.json` sets COOP/COEP +
SPA-fallback rewrites required for Compose Multiplatform Wasm features.

## Auth modes

1. **Static token (default):** set `vercel_token` alias via `/secrets push`.
2. **OIDC federation (recommended for org accounts):** configure the Vercel
   project to trust GitHub Actions OIDC; the workflow exchanges a short-lived
   JWT and no static token is needed. Documented in Vercel's "GitHub
   integration" docs.

## Local deploy

```bash
./gradlew :cmp-web:jsBrowserDistribution
vercel deploy --prod --token=$VERCEL_TOKEN cmp-web/build/dist/js/productionExecutable
```

## Secrets required

| Alias              | Env                  |
|--------------------|----------------------|
| `vercel_token`     | `VERCEL_TOKEN`       |
| `vercel_org_id`    | `VERCEL_ORG_ID`      |
| `vercel_project_id`| `VERCEL_PROJECT_ID`  |
