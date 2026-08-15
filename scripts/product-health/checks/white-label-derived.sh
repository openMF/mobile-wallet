#!/usr/bin/env bash
# white-label-derived.sh — gradle/fork.properties must be DERIVED from app-profile, not hand-authored.
#
# app-profile/{app.yaml, platforms/**} is the single fork source of truth. scripts/white-label/derive.rb
# emits gradle/fork.properties from it (stamped with a DERIVED header). A hand-authored fork.properties
# is drift: it diverges from the SoT and won't pick up template changes to the white-label machinery.
# This check keeps the SoT authoritative — run `white-label-doctor` (or `--fix`) to derive it.
#
# Contract: exit 0 PASS · 2 WARN (advisory; setup not yet on the SoT model) · 1 FAIL. Never blocks — a
# fork mid-migration shouldn't be gated, but the drift is surfaced so it gets fixed.
set -uo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
FP="$ROOT/gradle/fork.properties"
APP_PROFILE="$ROOT/app-profile/app.yaml"

# Not a white-label fork → not applicable.
[ -f "$APP_PROFILE" ] || { echo "not a white-label fork (no app-profile) — n/a"; exit 0; }

if [ ! -f "$FP" ]; then
  echo "⚠️  gradle/fork.properties absent — run white-label-doctor to derive it from app-profile"
  exit 2
fi

if head -3 "$FP" | grep -qiE 'DERIVED from app-profile'; then
  echo "✅ gradle/fork.properties is derived from app-profile (SoT authoritative)"
  exit 0
fi

echo "⚠️  gradle/fork.properties looks hand-authored (no DERIVED header) — it should be generated from"
echo "    app-profile/{app.yaml,platforms/**} (the single fork SoT). Run: scripts/white-label/doctor.sh --fix"
echo "    Hand-edits drift from the SoT + won't adopt template changes to the white-label machinery."
exit 2
