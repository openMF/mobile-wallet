# deployment/ios/appstore — App Store (production)

**Tier:** 3 (gated, opt-in)
**Owning capability:** `ios-app-store`
**Output:** signed IPA → App Store via `deliver`
**Gating:** `requires_confirm: true`

## What this deploys

App-Store-signed iOS IPA to the App Store via `deliver`. Wraps the Info.plist
mutation in a backup-then-restore (`AppStoreHelpers.with_plist_backup`) so the
working tree stays clean after the lane completes (success OR failure).

## Local deploy

```bash
# Double-confirmation gates the lane in production environments
bundle exec fastlane ios release
```

## Production gating

CI runs only via `workflow_dispatch` with a CONFIRM input. No push-trigger.

## Secrets required

Run `/secrets pull --required-for ios-signing,ios-app-store`. See `secrets-needs.yaml`.
