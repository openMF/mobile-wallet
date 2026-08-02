# deployment/desktop/windows-exe — GitHub Releases (Windows EXE)

**Tier:** 1 (default-enabled)
**Owning capability:** `github-releases-publish`
**Signing:** none — see `deployment/desktop/msi-signed/` for signed MSI option.

## What this deploys

Unsigned Windows EXE installer (Compose Multiplatform `packageReleaseExe`)
attached to a GitHub release.

## Local deploy

```bash
TAG=v2026.06.04 FLAVOR=prod bash deployment/desktop/windows-exe/script.sh
```

## CI

`workflow-snippet.yml` uses `windows-latest` + Git Bash (`shell: bash`) so the
script behaves identically on local + CI.
