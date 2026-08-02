#!/usr/bin/env bash
# deployment/_shared/scripts/materialize-ios-secrets.sh
#
# Manual-mode (no vault) secrets materialization for iOS lanes.
# Vault-mode is handled by secrets-pull.sh — this is the fallback.
#
# Env vars consumed (set by GHA workflow):
#   APPSTORE_AUTH_KEY_B64        Base64 ASC API .p8 key
#   APPSTORE_KEY_ID              ASC key id
#   APPSTORE_ISSUER_ID           ASC issuer id
#   MATCH_SSH_PRIVATE_KEY_B64    Base64 SSH key for Match repo
#   MATCH_PASSWORD               Match decryption password
#   FIREBASECREDS                Firebase App Distribution SA JSON (optional — firebase lane only)
set -euo pipefail

mkdir -p secrets/live/apple/appstore secrets/live/apple/match secrets

# App Store Connect API key (drives Match + pilot + deliver + notarize).
[[ -n "${APPSTORE_AUTH_KEY_B64:-}" ]] && echo "$APPSTORE_AUTH_KEY_B64" | base64 -d > secrets/live/apple/appstore/AuthKey.p8
[[ -n "${APPSTORE_KEY_ID:-}"       ]] && printf '%s' "$APPSTORE_KEY_ID"     > secrets/live/apple/appstore/key_id
[[ -n "${APPSTORE_ISSUER_ID:-}"    ]] && printf '%s' "$APPSTORE_ISSUER_ID"  > secrets/live/apple/appstore/issuer_id

# Match repo access.
if [[ -n "${MATCH_SSH_PRIVATE_KEY_B64:-}" ]]; then
  echo "$MATCH_SSH_PRIVATE_KEY_B64" | base64 -d > secrets/live/apple/match/match_ci_key
  chmod 600 secrets/live/apple/match/match_ci_key
fi
if [[ -n "${MATCH_PASSWORD:-}" ]]; then
  printf '%s' "$MATCH_PASSWORD" > secrets/live/apple/match/.match_password
  chmod 600 secrets/live/apple/match/.match_password
fi

# Firebase distribution (only firebase lane).
[[ -n "${FIREBASECREDS:-}" ]] && printf '%s' "$FIREBASECREDS" > secrets/live/android/firebaseAppDistributionServiceCredentialsFile.json

echo "✅ iOS secrets materialized (manual-mode)"
