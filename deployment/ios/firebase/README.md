# deployment/ios/firebase — iOS Firebase App Distribution

**Tier:** 1 (default-enabled)
**Owning capability:** `ios-firebase-distribute`
**Output:** signed IPA → Firebase

## What this deploys

Ad-hoc-signed iOS IPA to Firebase App Distribution for internal testers.
Replaces the legacy `deploy_on_firebase` lane from `fastlane/Fastfile`.

## Local deploy

```bash
bundle exec fastlane ios deploy_on_firebase
```

Requires macOS + Xcode + CocoaPods.

## Secrets required

Run `/secrets pull --required-for ios-signing,ios-firebase-distribute`.

| Alias                                              | Canonical                                                       |
|----------------------------------------------------|-----------------------------------------------------------------|
| `appstore_connect_api_key_p8`                      | `secrets/apple/appstore/AuthKey.p8`                                            |
| `match_git_ssh_private_key`                        | `secrets/apple/match/match_ci_key`                                          |
| `match_password`                                   | `ENV:MATCH_PASSWORD`                                            |
| `firebase_app_distribution_service_account_json`   | `secrets/android/firebaseAppDistributionServiceCredentialsFile.json`    |

## Troubleshooting

- **"Could not find a matching code signing identity"** — Match repo empty / SSH key bad. `bundle exec fastlane match adhoc --readonly`.
- **"groups parameter ignored"** — fixed: `FirebaseHelpers.resolve_groups()` honors `options[:tester_groups]` → `ENV['FIREBASE_GROUPS']` → config.
