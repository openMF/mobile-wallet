#!/usr/bin/env bash
# checks/keychain-cleanup.sh — setup_ci's throwaway keychain must be GUARANTEED-cleaned so it never
# lingers as the macOS default (→ endless "wants to use fastlane_tmp_keychain" prompts from unrelated
# apps). Motivating incident 2026-08-08: a SELF-HOSTED GitHub Actions runner on a dev's Mac ran iOS/mac
# lanes; cancelled jobs left fastlane_tmp default.
#
#   KC-1  a cleanup_ci_keychain helper exists.
#   KC-2  it is guarded on RUNNER_ENVIRONMENT (skip ephemeral github-hosted) — NOT bare ENV["CI"]
#         (a self-hosted runner sets CI=true but is a PERSISTENT machine that MUST be cleaned).
#   KC-3  it is SURGICAL — never rewrites the whole search list (`list-keychains -s …login`), which
#         would drop the user's other keychains; touches only fastlane_tmp.
#   KC-4  cleanup is wired start-of-run (before_all) AND end-of-run (after_all + error).
# exit 0 PASS · 1 FAIL. Pure bash + grep.
set -uo pipefail
ROOT="${HEALTH_ROOT:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}"
CFG="$ROOT/deployment/_shared/config.rb"
BA="$ROOT/deployment/_shared/before_all.rb"
fail=0

grep -q 'def cleanup_ci_keychain' "$CFG" 2>/dev/null || { echo "❌ KC-1: no cleanup_ci_keychain helper in config.rb"; fail=1; }
grep -qE 'RUNNER_ENVIRONMENT.*github-hosted' "$CFG" 2>/dev/null \
  || { echo "❌ KC-2: cleanup not guarded on RUNNER_ENVIRONMENT (bare ENV[\"CI\"] would skip self-hosted runners)"; fail=1; }
grep -qE 'list-keychains.*login\.keychain' "$CFG" 2>/dev/null \
  && { echo "❌ KC-3: cleanup rewrites the whole search list to login-only — drops the user's other keychains"; fail=1; } \
  || true
grep -qE 'delete-keychain' "$CFG" 2>/dev/null || { echo "❌ KC-3: cleanup does not delete the fastlane_tmp keychain"; fail=1; }
{ grep -q 'cleanup_ci_keychain' "$BA" && grep -q 'after_all' "$BA" && grep -q 'error do' "$BA"; } \
  || { echo "❌ KC-4: cleanup not wired into before_all + after_all + error (before_all.rb)"; fail=1; }

[ "$fail" = 0 ] && { echo "✅ keychain-cleanup: cleanup_ci_keychain (KC-1) + RUNNER_ENVIRONMENT guard (KC-2) + surgical (KC-3) + start/end-of-run wiring (KC-4)"; exit 0; }
exit 1
