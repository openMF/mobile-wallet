#!/usr/bin/env bash
# deployment/_shared/scripts/materialize-android-secrets.sh
#
# Manual-mode (no vault) secrets materialization for Android lanes.
# Vault-mode is handled by secrets-pull.sh — this script is the fallback when
# the vault is unavailable, reading GitHub Secrets from env vars.
#
# Env vars consumed (set by GHA workflow):
#   GOOGLESERVICES                      Google Services JSON content
#   PLAYSTORECREDS                      Play Store SA JSON content
#   FIREBASECREDS                       Firebase App Distribution SA JSON content
#   UPLOAD_KEYSTORE_FILE                Base64 Play Console upload keystore (Play App Signing)
#   UPLOAD_KEYSTORE_FILE_PASSWORD       Upload keystore password
#   UPLOAD_KEYSTORE_ALIAS               Upload keystore alias
#   UPLOAD_KEYSTORE_ALIAS_PASSWORD      Upload keystore alias password
#
# Play App Signing: Google holds the app signing key (KMS) — developer only holds
# the UPLOAD key. One keystore family is materialized.
set -euo pipefail

mkdir -p secrets/live/android/keystores cmp-android/src/prod cmp-android/src/demo

# Google Services — required for prod + demo flavors.
if [[ -n "${GOOGLESERVICES:-}" ]]; then
  printf '%s' "$GOOGLESERVICES" > cmp-android/src/prod/google-services.json
  printf '%s' "$GOOGLESERVICES" > cmp-android/src/demo/google-services.json
fi

# Play Store + Firebase distribution credentials.
[[ -n "${PLAYSTORECREDS:-}"  ]] && printf '%s' "$PLAYSTORECREDS"  > secrets/live/android/playStorePublishServiceCredentialsFile.json
[[ -n "${FIREBASECREDS:-}"   ]] && printf '%s' "$FIREBASECREDS"   > secrets/live/android/firebaseAppDistributionServiceCredentialsFile.json

# UPLOAD keystore — the ONE keystore the developer holds under Play App Signing.
# Gradle's signingConfig reads from secrets/live/android/keystores/upload_keystore.keystore.
# Google's KMS holds the app signing key; it's never materialized locally.
if [[ -n "${UPLOAD_KEYSTORE_FILE:-}" ]]; then
  echo "$UPLOAD_KEYSTORE_FILE" | base64 -d > secrets/live/android/keystores/upload_keystore.keystore
fi

echo "✅ Android secrets materialized (manual-mode)"
