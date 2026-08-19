# deployment/desktop/dmg-notarized — Notarized macOS DMG

**Tier:** 2 (opt-in)
**Owning capability:** `macos-signing`
**Signing:** Developer ID Application cert (Fastlane Match) + `notarize` action (notarytool + stapler)
**Gating:** `requires_confirm: true`

## What this deploys

Apple-notarized macOS DMG attached to an existing GitHub release. Match installs
the Developer ID Application cert + private key into the keychain; the Fastlane
`notarize` action calls notarytool, waits for Apple's approval, then staples the
ticket onto the DMG so it verifies offline. Users get no Gatekeeper warning.

## Cost / time note

- Apple Developer Program: $99/yr.
- Notarization wait: 5-15 minutes (`requires_confirm: true` gates CI usage).

## One-time bootstrap (maintainer only)

Match needs a Developer ID Application cert in its repo before any CI run can
succeed. Seed it once from a maintainer machine:

```bash
(cd deployment && bundle exec fastlane match developer_id --platform macos)
```

Every CI run after that uses `readonly: true` and only consumes the cert.

## Local deploy

```bash
export GIT_TAG=v2026.06.04
(cd deployment && bundle exec fastlane mac buildNotarizedMacDmg)
```

## Local fast-iteration build (trusted-device, ad-hoc signed)

Skips Match + notarize + GH upload — finishes in build time only, not 5-15 min
notarytool wait. The output DMG is **not** distributable.

```bash
(cd deployment && AD_HOC_SIGNING=1 bundle exec fastlane mac buildNotarizedMacDmg)
```

## Secrets required

```bash
/secrets pull --required-for macos-signing,desktop-dmg-notarized
```

5 aliases — ASC API key (`AuthKey.p8` + `key_id` + `issuer_id`) and Match repo
access (`match_ci_key` + `MATCH_PASSWORD`). See `secrets-needs.yaml`. The
Developer ID `.p12` itself does NOT appear here — Match owns it.

## Why Fastlane (not the legacy `script.sh`)

The bash version hand-rolled `security import` + `xcrun notarytool` + `xcrun stapler` + `gh release upload`.
Replacing each with native Fastlane actions (`match`, `notarize`) eliminates:

- The `DEVELOPER_ID_CERT_B64` + `DEVELOPER_ID_CERT_PASSWORD` + `DEVELOPER_ID_NAME`
  GitHub secrets (Match owns the cert lifecycle).
- The manual `security create-keychain` + `security import` + `set-key-partition-list` steps.
- Two extra GHA secrets per fork.

See `lane.rb` for the full mapping from custom bash → Fastlane action.
