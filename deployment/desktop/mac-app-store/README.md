# deployment/desktop/mac-app-store — Mac App Store

**Tier:** 2 (opt-in)
**Owning capability:** `desktop-mac-app-store`
**Output:** signed `.pkg` → TestFlight (Mac) → Mac App Store
**Gating:** `requires_confirm: true`

## What this deploys

Two lanes:
- `desktop_testflight` — uploads to TestFlight (Mac App Store track).
- `desktop_release` — submits to Mac App Store production.

Both lifted verbatim from the interim `platform :mac do` block that lived in
`fastlane/Fastfile` after SP-03; that block was deleted from `fastlane/Fastfile`
as part of SP-08.

## Local deploy

```bash
# TestFlight Mac
bundle exec fastlane mac desktop_testflight pkg_path:cmp-desktop/build/release/main/pkg/MoneyToolkit.pkg

# Mac App Store production
bundle exec fastlane mac desktop_release pkg_path:cmp-desktop/build/release/main/pkg/MoneyToolkit.pkg \
  submit_for_review:true automatic_release:false
```

## Secrets required

Run `/secrets pull --required-for macos-signing,desktop-mac-app-store`.

## Dev fallback

`dev_fallback.signing_mode: skip-codesigning` — local dev builds skip the cert
chain (build only, no upload). Production CI always uses the full signed path.
