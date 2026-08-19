# deployment/desktop/macos-dmg-unsigned — GitHub Releases (macOS DMG, unsigned)

**Tier:** 1 (default-enabled)
**Owning capability:** `github-releases-publish`
**Signing:** none → users see Gatekeeper warning on first run.

## What this deploys

Unsigned macOS DMG attached to a GitHub release. For a notarized build that
runs without Gatekeeper warnings, see `deployment/desktop/dmg-notarized/`.

## Local deploy

```bash
TAG=v2026.06.04 FLAVOR=prod bash deployment/desktop/macos-dmg-unsigned/script.sh
```
