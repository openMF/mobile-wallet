# Firebase Service Account

Used for: Firebase App Distribution (upload APKs/IPAs to testers).
Also used to fetch the latest Firebase build number for auto-increment.

## How to get it

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project (or create one)
3. **Project Settings** (gear icon) → **Service accounts** tab
4. Click **Generate new private key**
5. Confirm and download the JSON file
6. Copy it to `secrets/live/android/firebase/firebaseAppDistributionServiceCredentialsFile.json`

## Firebase App IDs

You also need the Firebase App ID for each platform:

1. In Firebase Console → **Project settings** → **General**
2. Scroll to "Your apps" section
3. Copy the **App ID** for each app (format: `1:123456789:android:abc123`)

| File | Content | GHA Secret |
|------|---------|------------|
| `secrets/live/android/firebase/firebaseAppDistributionServiceCredentialsFile.json` | Service account JSON | `FIREBASECREDS` |
| `secrets/live/android/firebase/android_app_id` | Android prod App ID | `FIREBASE_ANDROID_APP_ID` |
| `secrets/live/android/firebase/android_demo_app_id` | Android demo App ID | `FIREBASE_ANDROID_DEMO_APP_ID` |
| `secrets/live/apple/firebase/ios_app_id` | iOS App ID | `FIREBASE_IOS_APP_ID` |

## Commands

```bash
mkdir -p secrets/live/android/firebase
cp secrets/sample/android/firebase/firebaseAppDistributionServiceCredentialsFile.json secrets/live/android/firebase/firebaseAppDistributionServiceCredentialsFile.json
# Replace with real JSON from Firebase Console

echo "1:123456789:android:abc123" > secrets/live/android/firebase/android_app_id
echo "1:123456789:android:def456" > secrets/live/android/firebase/android_demo_app_id
echo "1:123456789:ios:ghi789"     > secrets/live/apple/firebase/ios_app_id

# Sync to GitHub
bash scripts/secrets/sync-secrets-to-github.sh --only firebase
```

## Tester groups

Set `FIREBASE_GROUPS` to a comma-separated list of Firebase tester group names:
```
FIREBASE_GROUPS=internal-testers,qa-team
```
This can be set as `apple.tf.groups` in `gradle/fork.properties` or as a GitHub Actions variable.

## Notes

- Service accounts don't expire but should be rotated annually
- The service account only needs Firebase App Distribution roles — not full admin
- The App ID is NOT a secret — it can be in version control, but we keep it here for convenience
