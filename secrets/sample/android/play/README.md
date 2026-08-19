# Google Play Store Service Account

Used for: uploading AABs to Play Store internal/beta tracks, promoting releases.

## How to create it

### Step 1: Create service account in Google Cloud Console

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Select the project linked to your Play Console
3. **IAM & Admin** → **Service Accounts** → **+ Create service account**
4. Name: `fastlane-play-publisher`
5. Click **Create and continue**
6. Skip "Grant access" and "Grant users access" — click **Done**
7. Click the new service account → **Keys** tab → **Add key** → **Create new key**
8. Choose **JSON** → **Create**
9. The JSON file downloads automatically

### Step 2: Grant Play Console access

1. Go to [Google Play Console](https://play.google.com/console)
2. **Setup** → **API access**
3. Link to the Google Cloud project (if not already linked)
4. Under "Service accounts", find your account → click **Grant access**
5. Grant these permissions:
   - **Release manager** (to upload AABs and manage releases)
   - **View app information** (minimum read access)
6. Click **Invite user** to confirm

### Step 3: Place the file

```bash
mkdir -p secrets/live/android/play
cp ~/Downloads/your-project-*.json secrets/live/android/play/playStorePublishServiceCredentialsFile.json
```

## File

| File | Content | GHA Secret |
|------|---------|------------|
| `secrets/live/android/play/playStorePublishServiceCredentialsFile.json` | Service account JSON | `PLAYSTORECREDS` |

## Sync to GitHub

```bash
bash scripts/secrets/sync-secrets-to-github.sh --only play
```

## Notes

- Play Console propagation can take up to 24 hours after granting access
- Service account email shows as an internal tester in Play Console — normal
- Rotate by generating a new JSON key and revoking the old one
- The `promote_to_production` lane requires this to be set (gated by requires_confirm in config)
