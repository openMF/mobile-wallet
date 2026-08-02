#!/usr/bin/env bash
# deployment/_shared/scripts/materialize-mac-secrets.sh
#
# Manual-mode (no vault) secrets materialization for macOS lanes.
# Superset of the iOS materialization — adds Mac App Store cert + Mac Installer cert.
# Match-managed Developer ID Application cert (for dmg-notarized) does NOT
# appear here — Match installs it from the Match repo at lane runtime.
#
# Env vars consumed (set by GHA workflow):
#   (all iOS env vars — APPSTORE_AUTH_KEY_B64, APPSTORE_KEY_ID, APPSTORE_ISSUER_ID,
#    MATCH_SSH_PRIVATE_KEY_B64, MATCH_PASSWORD)
#   MAC_APP_STORE_CERT_B64       Base64 Mac App Distribution .p12 (manual-only; Match preferred)
#   MAC_INSTALLER_CERT_B64       Base64 3rd Party Mac Developer Installer .p12 (manual-only)
set -euo pipefail

# Reuse iOS materialization for ASC + Match — same paths.
bash "$(dirname "$0")/materialize-ios-secrets.sh"

# Mac-specific cert .p12 (consumed by mac-app-store/lane.rb when Match isn't wired).
[[ -n "${MAC_APP_STORE_CERT_B64:-}" ]] && echo "$MAC_APP_STORE_CERT_B64" | base64 -d > secrets/live/desktop/macos/app_store.p12
[[ -n "${MAC_INSTALLER_CERT_B64:-}" ]] && echo "$MAC_INSTALLER_CERT_B64" | base64 -d > secrets/live/desktop/macos/installer.p12

echo "✅ macOS secrets materialized (manual-mode)"
