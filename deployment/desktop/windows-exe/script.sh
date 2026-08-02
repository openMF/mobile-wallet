#!/usr/bin/env bash
# deployment/desktop/windows-exe/script.sh
# Tier-1 unsigned Windows EXE → GitHub Releases.
# Runs on a windows-latest runner under Git Bash, or under WSL locally.
set -euo pipefail

FLAVOR="${FLAVOR:-prod}"
TAG="${TAG:?GitHub release tag must be set (e.g. v2026.06.04)}"
STAGE="${STAGE:-stable}"   # prerelease | beta | stable — direct-distro promotion ladder

# 1. Build the EXE installer via Compose Desktop.
./gradlew :cmp-desktop:packageReleaseExe -PflavorDimension="$FLAVOR"

# 2. Locate the artifact.
EXE_PATH="$(find cmp-desktop/build/compose/binaries/main-release/exe -name '*.exe' -type f | head -1)"
[[ -f "$EXE_PATH" ]] || { echo "EXE not found at expected path"; exit 1; }

# 3. Upload to the GH Release.
gh release upload "$TAG" "$EXE_PATH" --clobber

# 4. Apply promotion-stage flags (prerelease + latest) per the direct-distro ladder.
bash "$(dirname "$0")/../../_shared/scripts/gh-release-stage.sh" "$TAG" "$STAGE"
