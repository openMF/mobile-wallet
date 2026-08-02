# Secrets Setup Checklist

Use this checklist when setting up a new fork. Complete the platforms you intend to deploy to.

## Quick start

```bash
# 1. Copy the full structure into secrets/live/
cp -r secrets/sample/* secrets/live/

# 2. Work through the sections below, replacing placeholder files with real ones

# 3. Sync everything to GitHub Actions
bash scripts/secrets/sync-secrets-to-github.sh --dry-run   # preview first
bash scripts/secrets/sync-secrets-to-github.sh              # push all secrets
```

---

## iOS — required for TestFlight / App Store

### 1. App Store Connect API Key

See [secrets/sample/apple/appstore/README.md](apple/appstore/README.md) for detailed steps.

- [ ] Create API key at [App Store Connect → Keys](https://appstoreconnect.apple.com/access/api)
- [ ] Place `.p8` file at `secrets/live/apple/appstore/AuthKey.p8`
- [ ] Write Key ID (10 chars) to `secrets/live/apple/appstore/key_id`
- [ ] Write Issuer ID (UUID) to `secrets/live/apple/appstore/issuer_id`

### 2. Apple Developer Team ID

- [ ] Find Team ID at [Apple Developer → Membership](https://developer.apple.com/account)
- [ ] Set `apple.team.id` in `gradle/fork.properties` (copy from `gradle/fork.properties.template`)

### 3. Fastlane Match (Code Signing)

See [secrets/sample/apple/match/README.md](apple/match/README.md) for detailed steps.

- [ ] Create private GitHub repo for certificates (e.g. `your-org/ios-certificates`)
- [ ] Generate SSH key: `ssh-keygen -t ed25519 -f secrets/live/apple/match/match_ci_key -N ""`
- [ ] Add `secrets/live/apple/match/match_ci_key.pub` as deploy key to the cert repo (write access)
- [ ] Generate password: `openssl rand -base64 32 | tr -d '\n' > secrets/live/apple/match/.match_password`
- [ ] Set `apple.match.git.url=git@github.com:your-org/ios-certificates.git` in `gradle/fork.properties`
- [ ] Run `bundle exec fastlane match init` once (first fork only)

### 4. Contact information (for TestFlight and App Store review)

Edit `gradle/fork.properties` (copy from `gradle/fork.properties.template`) and fill in:

- [ ] `org.email` / `org.first.name` / `org.last.name` / `org.phone`
- [ ] `apple.tf.groups` (comma-separated TestFlight tester group names)
- [ ] `org.marketing.url` / `org.privacy.url` / `org.support.url`

### 5. APN Push Notifications (optional)

See [secrets/sample/apple/apn/README.md](apple/apn/README.md).

- [ ] Create APN key at [Apple Developer → Keys](https://developer.apple.com/account/resources/authkeys/list)
- [ ] Place `.p8` at `secrets/live/apple/apn/APNAuthKey.p8`
- [ ] Write Key ID to `secrets/live/apple/apn/key_id`
- [ ] Write Team ID to `secrets/live/apple/apn/team_id`

---

## Android — required for Play Store

### 6. Release Keystore

See [secrets/sample/android/keystores/README.md](android/keystores/README.md) for detailed steps.

- [ ] Generate keystore: `keytool -genkey -keystore secrets/live/android/keystores/upload_keystore.keystore -alias upload -keyalg RSA -keysize 4096 -validity 10000`
- [ ] Create `secrets/live/android/keystores/upload_keystore.properties` with `storePassword`, `keyAlias`, `keyPassword`
- [ ] **Back up the keystore** — you cannot recover it if lost

### 7. Google Play Service Account

See [secrets/sample/android/play/README.md](android/play/README.md) for detailed steps.

- [ ] Create service account in Google Cloud Console
- [ ] Grant service account access in Play Console (Release Manager role)
- [ ] Place JSON key at `secrets/live/android/play/playStorePublishServiceCredentialsFile.json`

---

## Firebase — required for Firebase App Distribution (iOS + Android)

See [secrets/sample/android/firebase/README.md](android/firebase/README.md) for detailed steps.

- [ ] Download service account JSON from Firebase Console → Project Settings → Service Accounts
- [ ] Place at `secrets/live/android/firebase/firebaseAppDistributionServiceCredentialsFile.json`
- [ ] Write Android prod App ID to `secrets/live/android/firebase/android_app_id`
- [ ] Write Android demo App ID to `secrets/live/android/firebase/android_demo_app_id`
- [ ] Write iOS App ID to `secrets/live/apple/firebase/ios_app_id`
- [ ] Set `apple.tf.groups` in `gradle/fork.properties` (Firebase tester group names)

---

## Web hosting — required for web deployment

### Cloudflare Pages
- [ ] [cloudflare/README.md](web/cloudflare/README.md) → `secrets/live/web/cloudflare/api_token`, `secrets/live/web/cloudflare/account_id`

### Netlify
- [ ] [netlify/README.md](web/netlify/README.md) → `secrets/live/web/netlify/auth_token`, `secrets/live/web/netlify/site_id`

### Vercel
- [ ] [vercel/README.md](web/vercel/README.md) → `secrets/live/web/vercel/token`, `secrets/live/web/vercel/org_id`, `secrets/live/web/vercel/project_id`

---

## Desktop — OPTIONAL (only for OS-trusted *signed* installers)

Unsigned EXE/MSI/DMG/DEB build + publish to GitHub Releases without any of these.
Fill them only when you want notarized/signed desktop artifacts.

### macOS (.app / .pkg)
- [ ] [desktop/macos/README.md](desktop/macos/README.md) → `secrets/live/desktop/macos/app_store.p12`, `secrets/live/desktop/macos/installer.p12`

### Windows (Authenticode)
- [ ] [desktop/windows/README.md](desktop/windows/README.md) → `secrets/live/desktop/windows/code_signing.pfx`, `secrets/live/desktop/windows/code_signing_password`

### Linux (GPG-signed packages)
- [ ] [desktop/linux/README.md](desktop/linux/README.md) → `secrets/live/desktop/linux/gpg_signing.key`, `secrets/live/desktop/linux/gpg_passphrase`

---

## Final step: sync to GitHub Actions

```bash
# Verify secrets/live/ looks right
ls -la secrets/live/
ls -la secrets/live/apple/appstore/ secrets/live/apple/match/ secrets/live/android/firebase/ secrets/live/android/play/ secrets/live/android/keystores/

# Dry run to preview what will be set
bash scripts/secrets/sync-secrets-to-github.sh --dry-run

# Sync all
bash scripts/secrets/sync-secrets-to-github.sh

# Or sync by category
bash scripts/secrets/sync-secrets-to-github.sh --only ios
bash scripts/secrets/sync-secrets-to-github.sh --only android
bash scripts/secrets/sync-secrets-to-github.sh --only firebase
bash scripts/secrets/sync-secrets-to-github.sh --only web
```

Once synced, trigger a workflow:

```bash
gh workflow run .github/workflows/multi-platform-build-and-publish.yml
```
