# deployment/android/play-closed — Play Store closed testing / alpha track (promotion)

**Tier:** 2
**Owning capability:** `android-play-internal-publish` (re-uses publisher service account)
**Output:** none — promotion-only

## What this does

Promotes the current Play Store internal track release to the closed testing (alpha) track.
**No build.** A successful `android-play-internal` deploy must precede this. Sits between
internal and beta in the ladder: internal → closed → beta → production.

## Local deploy

```bash
bundle exec fastlane android promoteToClosed
```
