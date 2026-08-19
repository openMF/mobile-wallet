#!/usr/bin/env bash
# deployment/desktop/msi-signed/script.sh
# Tier-2 Azure Trusted-Signing signed Windows MSI → GitHub Releases.
# Used by both local (with signtool installed) and CI (under Git Bash on
# windows-latest). CI wires Azure OIDC via azure/trusted-signing-action before
# invoking this script — see workflow-snippet.yml.
set -euo pipefail

FLAVOR="${FLAVOR:-prod}"
TAG="${TAG:?GitHub release tag must be set}"
STAGE="${STAGE:-stable}"   # prerelease | beta | stable — direct-distro promotion ladder

SIGNTOOL="${SIGNTOOL:?SIGNTOOL env var required (path to signtool.exe; CI exports it via azure/trusted-signing-action)}"
AZURE_SIGNING_DLIB="${AZURE_SIGNING_DLIB:?Azure dlib path from trusted-signing-action}"
AZURE_METADATA_FILE="${AZURE_METADATA_FILE:?Azure metadata file path from trusted-signing-action}"

# 1. Build MSI via Compose Desktop.
./gradlew :cmp-desktop:packageReleaseMsi -PflavorDimension="$FLAVOR"

MSI_PATH="$(find cmp-desktop/build/compose/binaries/main-release/msi -name '*.msi' -type f | head -1)"
[[ -f "$MSI_PATH" ]] || { echo "MSI not found"; exit 1; }

# 2. Sign with Azure Trusted Signing (signtool with Azure dlib).
"$SIGNTOOL" sign \
  /tr "http://timestamp.acs.microsoft.com" \
  /td sha256 \
  /fd sha256 \
  /dlib "$AZURE_SIGNING_DLIB" \
  /dmdf "$AZURE_METADATA_FILE" \
  "$MSI_PATH"

# 3. Upload to the GH Release.
gh release upload "$TAG" "$MSI_PATH" --clobber

# 4. Apply promotion-stage flags (prerelease + latest) per the direct-distro ladder.
bash "$(dirname "$0")/../../_shared/scripts/gh-release-stage.sh" "$TAG" "$STAGE"
