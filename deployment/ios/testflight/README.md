# deployment/ios/testflight — TestFlight

**Tier:** 1 (default-enabled)
**Owning capability:** `ios-testflight`
**Output:** App-Store-signed IPA → TestFlight (external + internal groups)

## What this deploys

App-Store-signed iOS IPA to TestFlight via `pilot`. Auto-bumps the App-Store
version against the latest TestFlight build number (monotonic guarantee via
`AppStoreHelpers.bumped_version`).

## Local deploy

```bash
bundle exec fastlane ios beta
```

## Secrets required

Run `/secrets pull --required-for ios-signing,ios-testflight`. See `secrets-needs.yaml`.

## Version sanitization

Gradle version `2026.1.1-beta.0.9+abc123` → App Store version `2026.1.9`
(format `YYYY.M.{commitCount}`). Handled by `VersionHelpers.appstore_sanitize`.
