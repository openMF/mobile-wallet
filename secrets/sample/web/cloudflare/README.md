# Cloudflare Pages

Used for: deploying the web app to Cloudflare Pages.

## How to get credentials

### API Token

1. Go to [Cloudflare Dashboard](https://dash.cloudflare.com) → **My Profile** → **API Tokens**
2. Click **Create Token**
3. Use the **Edit Cloudflare Workers** template (or custom token)
4. Permissions needed:
   - **Zone** → **Zone** → Read
   - **Account** → **Cloudflare Pages** → Edit
5. Set **Account Resources** to your account
6. Click **Continue to summary** → **Create token**
7. Copy the token immediately (shown only once)

### Account ID

1. Go to [Cloudflare Dashboard](https://dash.cloudflare.com)
2. Select any domain (or go to **Workers & Pages**)
3. Copy the **Account ID** from the right sidebar

## Files

| File | Content | GHA Secret |
|------|---------|------------|
| `secrets/web/cloudflare/api_token` | API token string | `CLOUDFLARE_API_TOKEN` |
| `secrets/web/cloudflare/account_id` | Account ID string | `CLOUDFLARE_ACCOUNT_ID` |

## Commands

```bash
echo "your-api-token" > secrets/web/cloudflare/api_token
echo "your-account-id" > secrets/web/cloudflare/account_id
bash scripts/secrets/sync-secrets-to-github.sh --only web
```

## Notes

- The Project name is set in `deployment/web/cloudflare-pages/wrangler.toml`
- Cloudflare Pages auto-builds on push to production branch — disable this if
  you're deploying via GitHub Actions to avoid double-builds
