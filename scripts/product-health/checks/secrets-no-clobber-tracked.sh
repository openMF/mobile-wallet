#!/usr/bin/env bash
# checks/secrets-no-clobber-tracked.sh — `/secrets pull` (BuildSecrets#materialize!) must NEVER
# overwrite a git-TRACKED destination.
#
# Why: a LAYOUT `consume_at:` that points at a committed reference — e.g.
# `cmp-android/google-services.json` ships a COMPLETE per-flavor reference so the template builds
# out-of-box — IS the build's source of truth. Materializing the vault copy over it (which may be a
# SUBSET missing a flavored applicationId) silently breaks the flavored build at compile time, far
# from the pull that caused it (2026-08-09: the vault google-services carried 2 of 6 variants →
# demoDebug link failure). The guard: materialize! consults git_tracked?(dest) and returns BEFORE
# File.write when the destination is tracked — so committed references are never clobbered.
#
#   SNC-1  build_secrets.rb defines the git_tracked? guard.
#   SNC-2  materialize! consults git_tracked? BEFORE File.write (tracked dest short-circuits).
#
# exit 0 = PASS · 1 = FAIL. No deployment build_secrets.rb (non-KMP fork) → PASS (nothing to guard).
set -uo pipefail
# shellcheck source=scripts/product-health/lib.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/lib.sh"
: "${HEALTH_ROOT:?secrets-no-clobber-tracked: HEALTH_ROOT not set (run via product-health.sh)}"

BS="$HEALTH_ROOT/deployment/_shared/lib/build_secrets.rb"
[ -f "$BS" ] || { echo "no deployment build_secrets.rb — nothing to guard (ok)"; exit 0; }

fail=0

# SNC-1 — the guard method exists.
if ! grep -qE 'def git_tracked\?' "$BS"; then
  echo "${C_RED}✗ SNC-1${C_RST}: build_secrets.rb has no git_tracked? guard — /secrets pull can clobber tracked build references"
  fail=1
fi

# SNC-2 — inside materialize!, git_tracked? must appear BEFORE File.write.
body="$(awk '/def materialize!/{f=1} f{print} f&&/^    end$/{exit}' "$BS")"
gt_line="$(printf '%s\n' "$body" | grep -n 'git_tracked?'  | head -1 | cut -d: -f1)"
fw_line="$(printf '%s\n' "$body" | grep -n 'File\.write'   | head -1 | cut -d: -f1)"
if [ -z "$gt_line" ] || [ -z "$fw_line" ] || [ "$gt_line" -ge "$fw_line" ]; then
  echo "${C_RED}✗ SNC-2${C_RST}: materialize! must consult git_tracked? BEFORE File.write (a tracked dest must short-circuit, not be overwritten)"
  fail=1
fi

if [ "$fail" = 0 ]; then
  echo "${C_GRN}✓${C_RST} /secrets pull won't clobber git-tracked build references (materialize! guards on git_tracked?)"
  exit 0
fi
echo "  ↳ fix: in deployment/_shared/lib/build_secrets.rb#materialize!, return early (skip + warn) when git_tracked?(dest) is true — never File.write over a committed reference."
exit 1
