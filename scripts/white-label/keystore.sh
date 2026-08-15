#!/usr/bin/env bash
# scripts/white-label/keystore.sh — DEPRECATED thin shim (AC87 of fastlane-modernization epic)
#
# This file forwards to its new home under deployment/_shared/scripts/.
# Removed at template_version >= 2.7.0 (see fastlane-modernization sub-plan 06).
#
# Migration: update any caller to invoke
#   bash deployment/_shared/scripts/keystore-manager.sh "$@"
# directly.
set -euo pipefail

echo "[DEPRECATED] ./keystore-manager.sh is a transitional shim. " \
     "Call bash deployment/_shared/scripts/keystore-manager.sh \"\$@\" directly. " \
     "This shim is removed at template_version >= 2.7.0." >&2

exec bash deployment/_shared/scripts/keystore-manager.sh "$@"
