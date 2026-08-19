# App Store Connect API Key

Used for: TestFlight uploads, App Store submissions, Match certificate management.

## How to get it

1. Sign in to [App Store Connect](https://appstoreconnect.apple.com)
2. Go to **Users and Access** → **Integrations** → **App Store Connect API**
3. Click **+** to create a new key
4. Name: `Fastlane Deploy` (or similar)
5. Access: **App Manager** (minimum required for Fastlane)
6. Click **Generate**
7. **Download the `.p8` file immediately** — you can only download it once
8. Note the **Key ID** (10 characters, e.g. `ABC1234567`)
9. Note the **Issuer ID** (UUID, shown above the key list)

## Files to populate

| File | Content | GHA Secret |
|------|---------|------------|
| `secrets/apple/appstore/AuthKey.p8` | The downloaded `.p8` file (base64 in CI) | `APPSTORE_AUTH_KEY` |
| `secrets/apple/appstore/key_id` | 10-char Key ID, one line | `APPSTORE_KEY_ID` |
| `secrets/apple/appstore/issuer_id` | UUID Issuer ID, one line | `APPSTORE_ISSUER_ID` |

## Commands

```bash
# Copy placeholder structure
cp secrets/sample/apple/appstore/AuthKey.p8   secrets/live/apple/appstore/AuthKey.p8
cp secrets/sample/apple/appstore/key_id       secrets/live/apple/appstore/key_id
cp secrets/sample/apple/appstore/issuer_id    secrets/live/apple/appstore/issuer_id

# Then replace contents with real values
nano secrets/live/apple/appstore/key_id       # paste your 10-char Key ID
nano secrets/live/apple/appstore/issuer_id    # paste your UUID Issuer ID
cp ~/Downloads/AuthKey_*.p8 secrets/live/apple/appstore/AuthKey.p8

# Sync to GitHub
bash scripts/secrets/sync-secrets-to-github.sh --only ios
```

## Notes

- API keys are not tied to a specific app — one key works for all your apps
- Key permissions can be restricted to specific apps in App Store Connect
- Keys expire only when revoked — rotate annually as security best practice
- If you lose the `.p8` file you must revoke and regenerate (no re-download)
