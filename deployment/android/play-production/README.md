# deployment/android/play-production — Play Store production track (promotion)

**Tier:** 3 (gated, opt-in)
**Owning capability:** `android-play-prod-publish`
**Output:** none — promotion-only
**Gating:** `requires_confirm: true` (AC34)

## What this does

Promotes the current Play Store beta track release to the production track.
**No build.** A successful `android-play-beta` promote must precede this.

## Local deploy

```bash
# Double-confirmation required — never run from a script without a human gate
bundle exec fastlane android promote_to_production
```

## Production gating

This target is fenced behind `requires_confirm: true`. Every promotion path
(local, `/release promote`, GHA workflow_dispatch) enforces a `CONFIRM` input.

## Secrets required

Run `/secrets pull --required-for android-signing,android-play-prod-publish`.

| Alias                                            | Path                                                         |
|--------------------------------------------------|--------------------------------------------------------------|
| `kmp-project-template-upload-keystore`                  | `keystores/upload_keystore.keystore` (sign-verification)    |
| `google_play_publisher_service_account_json`     | `secrets/android/playStorePublishServiceCredentialsFile.json`        |
