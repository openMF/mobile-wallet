#!/usr/bin/env bash
# deployment/desktop/macos-dmg-unsigned/script.sh
# Tier-1 unsigned macOS DMG → GitHub Releases.
# Users will see a Gatekeeper warning; for a notarized build see ../dmg-notarized/.
set -euo pipefail

FLAVOR="${FLAVOR:-prod}"
TAG="${TAG:?GitHub release tag must be set (e.g. v2026.06.04)}"

# 1. Build the DMG via Compose Desktop.
./gradlew :cmp-desktop:packageReleaseDmg -PflavorDimension="$FLAVOR"

# 2. Locate the artifact.
DMG_PATH="$(find cmp-desktop/build/compose/binaries/main-release/dmg -name '*.dmg' -type f | head -1)"
[[ -f "$DMG_PATH" ]] || { echo "DMG not found at expected path"; exit 1; }

# 3. Upload to the GH Release.
gh release upload "$TAG" "$DMG_PATH" --clobber
