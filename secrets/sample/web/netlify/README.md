# Netlify

Used for: deploying the web app to Netlify.

## How to get credentials

### Auth Token

1. Go to [Netlify Dashboard](https://app.netlify.com) → **User Settings** → **Applications**
2. Under **Personal access tokens** → **New access token**
3. Name: `github-actions-deploy`
4. Copy the token (shown only once)

### Site ID

1. Go to your Netlify site dashboard
2. **Site configuration** → **General** → **Site information**
3. Copy the **Site ID** (UUID format)

## Files

| File | Content | GHA Secret |
|------|---------|------------|
| `secrets/web/netlify/auth_token` | Personal access token | `NETLIFY_AUTH_TOKEN` |
| `secrets/web/netlify/site_id` | Site UUID | `NETLIFY_SITE_ID` |

## Commands

```bash
echo "your-netlify-token" > secrets/web/netlify/auth_token
echo "your-site-id"       > secrets/web/netlify/site_id
bash scripts/secrets/sync-secrets-to-github.sh --only web
```
