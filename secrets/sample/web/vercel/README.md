# Vercel

Used for: deploying the web app to Vercel.

## How to get credentials

### Token

1. Go to [Vercel Dashboard](https://vercel.com/account/tokens)
2. Click **Create** → name it `github-actions-deploy`
3. Scope: **Full Account** (or scoped to the specific team)
4. Copy the token

### Org ID and Project ID

```bash
# After installing Vercel CLI and logging in:
npx vercel login
npx vercel link   # links your local project to Vercel
# Creates .vercel/project.json with orgId and projectId
cat .vercel/project.json
```

Or find them in the Vercel dashboard:
- **Org ID**: Account settings → General → **Team ID** (or personal account ID)
- **Project ID**: Project → Settings → General → **Project ID**

## Files

| File | Content | GHA Secret |
|------|---------|------------|
| `secrets/web/vercel/token` | API token | `VERCEL_TOKEN` |
| `secrets/web/vercel/org_id` | Org/Team ID | `VERCEL_ORG_ID` |
| `secrets/web/vercel/project_id` | Project ID | `VERCEL_PROJECT_ID` |

## Commands

```bash
echo "your-vercel-token"      > secrets/web/vercel/token
echo "your-org-id"            > secrets/web/vercel/org_id
echo "your-project-id"        > secrets/web/vercel/project_id
bash scripts/secrets/sync-secrets-to-github.sh --only web
```
