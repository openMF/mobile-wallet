# deployment/desktop/linux-deb — GitHub Releases (Linux DEB)

**Tier:** 1 (default-enabled)
**Owning capability:** `github-releases-publish`
**Signing:** none (community-friendly unsigned build)
**Output:** `.deb` uploaded to a GH release

## What this deploys

Unsigned Linux DEB package built by Compose Multiplatform → attached to a
GitHub release. No Fastlane involvement; this target is a thin `gradle +
gh release upload` shell pipeline.

## Local deploy

```bash
TAG=v2026.06.04 FLAVOR=prod bash deployment/desktop/linux-deb/script.sh
```

## CI

Wired via `workflow-snippet.yml`. Uses `${{ secrets.GITHUB_TOKEN }}` for upload.
