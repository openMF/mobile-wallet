# deployment/android/play-beta — Play Store beta track (promotion)

**Tier:** 2
**Owning capability:** `android-play-internal-publish` (re-uses publisher service account)
**Output:** none — promotion-only

## What this does

Promotes the current Play Store internal track release to the beta track.
**No build.** A successful `android-play-internal` deploy must precede this.

## Local deploy

```bash
bundle exec fastlane android promoteToBeta
```

## Secrets required

Only the Play publisher service account — no keystore (no build).

| Alias                                          | Path                                                         |
|------------------------------------------------|--------------------------------------------------------------|
| `google_play_publisher_service_account_json`   | `secrets/android/playStorePublishServiceCredentialsFile.json`        |
