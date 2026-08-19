#!/usr/bin/env bash
# checks/self-test-canaries.sh — runs every product-health canary (tests/*/run.sh) so the RED/GREEN
# fixtures are LOAD-BEARING in CI, not decorative. Discovered + executed by product-health.sh (which
# runs every checks/*.sh), which quality-gate.yml already invokes. Each canary is self-contained
# (own GREEN/RED fixtures) and deterministic — runs in both fork and TEMPLATE_SELF_BUILD context.
#
#   exit 0 = every canary PASS · exit 1 = one or more canaries FAIL (blocks).
#
# Locks (among others): flip-precondition-canary (guard↔YAML ownership drift, incl. og-images=generated)
# and sync-dirs-template-remote-canary (URL-match template resolution + ungated dry-run remote-add).
set -uo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"        # …/checks
TESTS_DIR="$(cd "$HERE/.." && pwd)/tests"                    # …/product-health/tests

shopt -s nullglob
canaries=("$TESTS_DIR"/*/run.sh)
[ "${#canaries[@]}" -eq 0 ] && { echo "self-test-canaries: no canaries found (skip)"; exit 0; }

fail=0
for run in "${canaries[@]}"; do
  name="$(basename "$(dirname "$run")")"
  out="$(bash "$run" 2>&1)"; rc=$?
  if [ "$rc" -eq 0 ]; then
    echo "  ✅ $name"
  else
    echo "  ❌ $name (exit $rc)"
    printf '%s\n' "$out" | sed 's/^/       /'
    fail=1
  fi
done
exit "$fail"
