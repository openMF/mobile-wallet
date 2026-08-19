# secrets/sample/ — OSS-safe schema-as-code

This tree is the **schema-as-code** mirror of `secrets/live/`. Every credential file
here is a PLACEHOLDER carrying a machine-detectable magic marker:

- **Text files:** first line is `# CLAUDE-PLACEHOLDER — do not commit this to secrets/`
- **JSON files:** `"_comment": "CLAUDE-PLACEHOLDER…"` key present

`secrets/live/` is gitignored. `secrets/sample/` is committed and shows exactly which
files to populate and where to get them.

## Quick start — fork setup

```bash
# 1. Copy placeholder structure
cp -r secrets/sample/* secrets/live/   # creates the directory structure
# WARNING: this copies placeholder files, not real secrets — you must fill them in

# 2. Fill in real values for the platforms you want to deploy to
#    Each subdirectory has a README.md explaining exactly how to get the credentials

# 3. Sync to GitHub Actions
bash scripts/secrets/sync-secrets-to-github.sh --dry-run   # preview what will be set
bash scripts/secrets/sync-secrets-to-github.sh              # push to your fork's GHA secrets

# 4. Trigger a workflow
gh workflow run .github/workflows/multi-platform-build-and-publish.yml
```

## Layout

> Every `secrets/sample/` path mirrors its `secrets/live/` peer 1:1 — same
> platform-namespaced layout (`android/` · `apple/` · `desktop/` · `web/`).
> Copy a subtree, fill the real value, done.

### Apple — iOS + macOS (one shared Apple team)

| secrets/sample/ path | secrets/live/ target | Credential | Per-secret guide |
|---|---|---|---|
| `apple/appstore/AuthKey.p8` | `secrets/live/apple/appstore/AuthKey.p8` | App Store Connect API key (.p8) | [apple/appstore/README.md](apple/appstore/README.md) |
| `apple/appstore/key_id` | `secrets/live/apple/appstore/key_id` | ASC Key ID (10 chars) | [apple/appstore/README.md](apple/appstore/README.md) |
| `apple/appstore/issuer_id` | `secrets/live/apple/appstore/issuer_id` | ASC Issuer ID (UUID) | [apple/appstore/README.md](apple/appstore/README.md) |
| `apple/match/match_ci_key` | `secrets/live/apple/match/match_ci_key` | SSH private key for Match repo | [apple/match/README.md](apple/match/README.md) |
| `apple/match/match_ci_key.pub` | `secrets/live/apple/match/match_ci_key.pub` | SSH public key (add to cert repo) | [apple/match/README.md](apple/match/README.md) |
| `apple/match/.match_password` | `secrets/live/apple/match/.match_password` | Match encryption password | [apple/match/README.md](apple/match/README.md) |
| `apple/apn/key_id` | `secrets/live/apple/apn/key_id` | APN Key ID (10 chars, optional) | [apple/apn/README.md](apple/apn/README.md) |
| `apple/apn/team_id` | `secrets/live/apple/apn/team_id` | APN Team ID = Apple Dev Team (optional) | [apple/apn/README.md](apple/apn/README.md) |
| `apple/apn/APNAuthKey.p8` | `secrets/live/apple/apn/APNAuthKey.p8` | APN push key (optional) | [Apple Developer](https://developer.apple.com/account/resources/authkeys/list) |

### Android — Play Store + signing

| secrets/sample/ path | secrets/live/ target | Credential | Per-secret guide |
|---|---|---|---|
| `android/keystores/upload_keystore.properties` | `secrets/live/android/keystores/upload_keystore.properties` | Keystore passwords/alias | [android/keystores/README.md](android/keystores/README.md) |
| `android/keystores/upload_keystore.keystore` | `secrets/live/android/keystores/upload_keystore.keystore` | Upload keystore binary | [android/keystores/README.md](android/keystores/README.md) |
| `android/play/playStorePublishServiceCredentialsFile.json` | `secrets/live/android/play/playStorePublishServiceCredentialsFile.json` | Play Store service account JSON | [android/play/README.md](android/play/README.md) |

### Firebase (Android + iOS — one Firebase project, all flavour packages)

| secrets/sample/ path | secrets/live/ target | Credential | Per-secret guide |
|---|---|---|---|
| `android/firebase/firebaseAppDistributionServiceCredentialsFile.json` | `secrets/live/android/firebase/firebaseAppDistributionServiceCredentialsFile.json` | Firebase service account JSON | [android/firebase/README.md](android/firebase/README.md) |
| `android/firebase/google-services.json` | `secrets/live/android/firebase/google-services.json` | google-services.json (all flavour packages) | [android/firebase/README.md](android/firebase/README.md) |
| `android/firebase/android_app_id` | `secrets/live/android/firebase/android_app_id` | Firebase Android prod App ID | [android/firebase/README.md](android/firebase/README.md) |
| `android/firebase/android_demo_app_id` | `secrets/live/android/firebase/android_demo_app_id` | Firebase Android demo App ID | [android/firebase/README.md](android/firebase/README.md) |
| `apple/firebase/ios_app_id` | `secrets/live/apple/firebase/ios_app_id` | Firebase iOS App ID | — |
| `apple/firebase/ios_demo_app_id` | `secrets/live/apple/firebase/ios_demo_app_id` | Firebase iOS demo App ID | — |
| `apple/firebase/ios_prod_app_id` | `secrets/live/apple/firebase/ios_prod_app_id` | Firebase iOS prod App ID | — |

### Desktop — signed release artifacts (placeholders — fill before a signed desktop release)

| secrets/sample/ path | secrets/live/ target | Credential | Per-secret guide |
|---|---|---|---|
| `desktop/macos/app_store.p12` | `secrets/live/desktop/macos/app_store.p12` | Mac App Store cert (.p12) | [desktop/macos/README.md](desktop/macos/README.md) |
| `desktop/macos/installer.p12` | `secrets/live/desktop/macos/installer.p12` | Mac Installer Distribution cert (.p12) | [desktop/macos/README.md](desktop/macos/README.md) |
| `desktop/windows/code_signing.pfx` | `secrets/live/desktop/windows/code_signing.pfx` | Windows Authenticode cert (.pfx) | [desktop/windows/README.md](desktop/windows/README.md) |
| `desktop/windows/code_signing_password` | `secrets/live/desktop/windows/code_signing_password` | .pfx password | [desktop/windows/README.md](desktop/windows/README.md) |
| `desktop/linux/gpg_signing.key` | `secrets/live/desktop/linux/gpg_signing.key` | GPG private key (ASCII-armored) | [desktop/linux/README.md](desktop/linux/README.md) |
| `desktop/linux/gpg_passphrase` | `secrets/live/desktop/linux/gpg_passphrase` | GPG key passphrase | [desktop/linux/README.md](desktop/linux/README.md) |

> Desktop signing is **optional** — unsigned `cmp-desktop` artifacts (EXE/MSI/DMG/DEB)
> build and publish to GitHub Releases without these. Fill them only when you want
> OS-trusted signed installers (notarized macOS, Authenticode Windows, signed Linux pkgs).

### Web hosting

| secrets/sample/ path | secrets/live/ target | Guide |
|---|---|---|
| `web/cloudflare/api_token` | `secrets/live/web/cloudflare/api_token` | [web/cloudflare/README.md](web/cloudflare/README.md) |
| `web/cloudflare/account_id` | `secrets/live/web/cloudflare/account_id` | [web/cloudflare/README.md](web/cloudflare/README.md) |
| `web/netlify/auth_token` | `secrets/live/web/netlify/auth_token` | [web/netlify/README.md](web/netlify/README.md) |
| `web/netlify/site_id` | `secrets/live/web/netlify/site_id` | [web/netlify/README.md](web/netlify/README.md) |
| `web/vercel/token` | `secrets/live/web/vercel/token` | [web/vercel/README.md](web/vercel/README.md) |
| `web/vercel/org_id` | `secrets/live/web/vercel/org_id` | [web/vercel/README.md](web/vercel/README.md) |
| `web/vercel/project_id` | `secrets/live/web/vercel/project_id` | [web/vercel/README.md](web/vercel/README.md) |

## Three deployment modes

### Mode 1 — Local / manual

Fill `gradle/fork.properties` (from `gradle/fork.properties.template`) with non-secret
identity/metadata (team ID, contacts, URLs). Then populate `secrets/live/` from the table above:

```bash
# Android
(cd deployment && bundle exec fastlane android deployReleaseApkOnFirebase)

# iOS
(cd deployment && bundle exec fastlane ios beta)
```

### Mode 2 — GitHub Actions

Run `scripts/secrets/sync-secrets-to-github.sh` once to push secrets to your fork's
GHA repository secrets. Workflows read them automatically via `secrets.*`.

```bash
bash scripts/secrets/sync-secrets-to-github.sh
```

### Mode 3 — Framework /release command

If you're using the Claude Product Cycle framework with a vault:

```bash
/secrets pull          # materializes vault → secrets/live/ canonical paths
/release ios testflight
```

## Authority

- RULE-SECRETS-VAULT-001 (SV2 schema-as-code parity)
- AC78 (≥14 OSS-safe files + README)
- AC83 (key-equivalence enforced)
- AC89 (magic markers on every placeholder)
