# deployment/android/firebase — Firebase App Distribution

**Tier:** 1 (default-enabled)
**Owning capability:** `android-firebase-distribute`
**Flavors:** `prod`, `demo`

## What this deploys

Production + demo Android APKs to Firebase App Distribution for internal tester
groups. Replaces the legacy `deployReleaseApkOnFirebase` + `deployDemoApkOnFirebase`
lanes from the monolithic `fastlane/Fastfile`.

## Local deploy

```bash
# Prod flavor
bundle exec fastlane android deployReleaseApkOnFirebase \
  storePassword:$KEYSTORE_PASSWORD \
  keyAlias:$KEYSTORE_ALIAS \
  keyPassword:$KEYSTORE_ALIAS_PASSWORD

# Demo flavor
bundle exec fastlane android deployDemoApkOnFirebase \
  storePassword:$KEYSTORE_PASSWORD \
  keyAlias:$KEYSTORE_ALIAS \
  keyPassword:$KEYSTORE_ALIAS_PASSWORD
```

## Secrets required

Run `/secrets pull --required-for android-signing,android-firebase-distribute` from
your bound project session. Or set the GHA manual-mode env vars listed in
`secrets-needs.yaml`.

| Alias                                              | Path                                                            |
|----------------------------------------------------|-----------------------------------------------------------------|
| `kmp_template_release_keystore`                    | `keystores/upload_keystore.keystore`                           |
| `firebase_app_distribution_service_account_json`   | `secrets/android/firebaseAppDistributionServiceCredentialsFile.json`    |

## Troubleshooting

- **"Keystore file not found"** — run `bash deployment/_shared/scripts/keystore-manager.sh check` or `/secrets pull`.
- **"groups parameter ignored"** — historical bug; this lane resolves groups via `FirebaseHelpers.resolve_groups()` which honors `options[:tester_groups]` → `ENV['FIREBASE_GROUPS']` → config default.
