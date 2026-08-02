# deployment/android/play-internal — Play Store internal track

**Tier:** 1 (default-enabled)
**Owning capability:** `android-play-internal-publish`
**Output:** signed AAB → Play internal track

## What this deploys

Production-flavor AAB to the Play Store internal track. Replaces the legacy
`deployInternal` lane from `fastlane/Fastfile`. The historical AC32 bug
(`gradle assemble` producing APK where Play required AAB) was already fixed in
SP-03 — this lane invokes `bundleProd`.

## Local deploy

```bash
bundle exec fastlane android deployInternal \
  storePassword:$KEYSTORE_PASSWORD \
  keyAlias:$KEYSTORE_ALIAS \
  keyPassword:$KEYSTORE_ALIAS_PASSWORD
```

## Secrets required

Run `/secrets pull --required-for android-signing,android-play-internal-publish`.

| Alias                                            | Path                                                         |
|--------------------------------------------------|--------------------------------------------------------------|
| `kmp_template_release_keystore`                  | `keystores/upload_keystore.keystore`                        |
| `google_play_publisher_service_account_json`     | `secrets/android/playStorePublishServiceCredentialsFile.json`        |
