# Android Keystore (Play App Signing model)

Single source of truth for Android release signing: **`upload_keystore.keystore`** (Play Console upload key) + **`upload_keystore.properties`** (credentials). One keystore — that's all you hold under Play App Signing.

## Why only one keystore?

Per [Google's official Play App Signing documentation](https://support.google.com/googleplay/android-developer/answer/9842756):

> *"By default, when you upload your app bundle, Play App Signing automatically generates a cryptographically strong RSA 4096-bit key to manage and protect your app. Over 90% of new apps use this recommended default."*

> *"Google manages and protects your app's signing key on the same secure infrastructure that Google uses to store its own keys. These keys are protected by Google's Key Management Service (KMS)."*

This means:

| Key type | Who holds it | What you do with it |
|---|---|---|
| **Upload key** | **You** — `upload_keystore.keystore` | Sign AABs before uploading to Play Console |
| **App signing key** | **Google Play (KMS)** | You can never download it. Google uses it to re-sign APKs delivered to users. |

The legacy two-keystore model only applies to apps published before 2021 or explicit Play App Signing opt-outs. This template assumes the modern default.

## File layout

```
secrets/android/keystores/
├── upload_keystore.keystore     ← the keystore binary
├── upload_keystore.properties   ← passwords + alias
└── README.md
```

## Generate a new upload keystore

```bash
keytool -genkey -v \
  -keystore secrets/android/keystores/upload_keystore.keystore \
  -alias upload \
  -keyalg RSA \
  -keysize 4096 \
  -validity 25000 \
  -dname "CN=Your Org, OU=Engineering, O=Your Org, L=City, S=State, C=US"
```

Then write `upload_keystore.properties`:

```properties
storeFile=upload_keystore.keystore
storePassword=YOUR_STORE_PASSWORD
keyAlias=upload
keyPassword=YOUR_KEY_PASSWORD
```

## Sync to GitHub Actions

```bash
bash scripts/secrets/sync-secrets-to-github.sh --only android
```

Pushes 4 secrets to the repo's GHA settings:
- `UPLOAD_KEYSTORE_FILE` (base64-encoded keystore binary)
- `UPLOAD_KEYSTORE_FILE_PASSWORD`
- `UPLOAD_KEYSTORE_ALIAS`
- `UPLOAD_KEYSTORE_ALIAS_PASSWORD`

Plus `GOOGLESERVICES` (Firebase config) if `secrets/android/firebase/google-services.json` exists.

## CRITICAL: back up your upload keystore

⚠️ **The upload keystore is your identity to Play Console**. Lose it and you have to request an upload key reset (Google takes 1-2 business days to approve), which means your CI is blocked until then. Back it up to:

- 1Password / Bitwarden secure note (base64-encoded)
- Encrypted cloud storage (separate from your Google account)
- Another team member's password manager

Google can reset the upload key if compromised — see [Request an upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756).

## Where Play Console reads from

`/release` → fastlane `android deployInternal` → reads `secrets/android/keystores/upload_keystore.keystore` per Gradle's `signingConfig` → signs AAB → uploads to Play Console → Play Console verifies signature against the registered upload key cert → re-signs with Google's app signing key → distributes.

If Play Console rejects with "Upload key mismatch", the local keystore's SHA-1 doesn't match Play Console's registered upload key cert. Verify with:

```bash
keytool -list -v -keystore secrets/android/keystores/upload_keystore.keystore -alias <your-alias>
# SHA-1 must match: Play Console → Setup → App integrity → "Upload key certificate"
```
