#!/usr/bin/env bash
# deployment/desktop/microsoft-store/script.sh
# Tier-2 Microsoft Store MSIX → Partner Center submission.
# Runs under Git Bash on windows-latest. Assumes MakeAppx.exe is on PATH or
# its absolute path is provided via MAKEAPPX env var.
set -euo pipefail

FLAVOR="${FLAVOR:-prod}"
MAKEAPPX="${MAKEAPPX:?MAKEAPPX env var required (path to MakeAppx.exe)}"
MS_PARTNER_CENTER_TENANT_ID="${MS_PARTNER_CENTER_TENANT_ID:?ms_partner_center_tenant_id required}"
MS_PARTNER_CENTER_CLIENT_ID="${MS_PARTNER_CENTER_CLIENT_ID:?ms_partner_center_client_id required}"
MS_PARTNER_CENTER_CLIENT_SECRET="${MS_PARTNER_CENTER_CLIENT_SECRET:?ms_partner_center_client_secret required}"
MS_APP_ID="${MS_APP_ID:?ms_app_id required}"

# 1. Build app payload (createReleaseDistributable produces /app/<App>.app).
./gradlew :cmp-desktop:createReleaseDistributable -PflavorDimension="$FLAVOR"

# 2. Stage MSIX layout: copy app payload + tokenized Package.appxmanifest (resolved from
#    gradle/fork.properties — the fork's identity SoT bridge) into a single tree.
STAGE="cmp-desktop/build/compose/binaries/main-release/msix-stage"
APP_LAYOUT="cmp-desktop/build/compose/binaries/main-release/app"
MANIFEST="$(dirname "$0")/Package.appxmanifest"
OUTPUT="cmp-desktop/build/compose/binaries/main-release/msix/mifos.msix"
FORK_PROPS="$(dirname "$0")/../../../gradle/fork.properties"
mkdir -p "$STAGE" "$(dirname "$OUTPUT")"
cp -R "$APP_LAYOUT/." "$STAGE/"

fork_prop() {
    grep -E "^$1=" "$FORK_PROPS" 2>/dev/null | head -1 | cut -d= -f2-
}
IDENTITY_NAME="$(fork_prop windows.msix.identity.name)"
IDENTITY_PUBLISHER="$(fork_prop windows.msix.identity.publisher)"
PUBLISHER_DISPLAY_NAME="$(fork_prop windows.msix.publisher.display.name)"
APP_DISPLAY_NAME="$(fork_prop app.display.name)"
APP_ID_NO_SPACES="$(printf '%s' "$APP_DISPLAY_NAME" | tr -d '[:space:]')"
sed \
    -e "s/__WINDOWS_MSIX_IDENTITY_NAME__/${IDENTITY_NAME}/g" \
    -e "s/__WINDOWS_MSIX_IDENTITY_PUBLISHER__/${IDENTITY_PUBLISHER}/g" \
    -e "s/__WINDOWS_MSIX_PUBLISHER_DISPLAY_NAME__/${PUBLISHER_DISPLAY_NAME}/g" \
    -e "s/__APP_DISPLAY_NAME__/${APP_DISPLAY_NAME}/g" \
    -e "s/__APP_ID_NO_SPACES__/${APP_ID_NO_SPACES}/g" \
    -e "s/__APP_EXECUTABLE__/${APP_DISPLAY_NAME}.exe/g" \
    "$MANIFEST" > "$STAGE/AppxManifest.xml"

# 3. Pack via MakeAppx.exe.
"$MAKEAPPX" pack /d "$STAGE" /p "$OUTPUT" /overwrite

# 4. Submit to Partner Center (StoreBroker config is at ./StoreBroker.config.json).
# In CI we invoke microsoft/StoreBroker-Action; locally invoke `Submit-StoreBrokerSubmission`
# via PowerShell. This bash path documents the contract; PS variant is preferred locally.
echo "Submission step is wired through GH Action (CI) or PowerShell StoreBroker module (local)."
echo "Package built at: $OUTPUT"
