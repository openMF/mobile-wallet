#!/usr/bin/env bash
# run.sh — RED/GREEN canary for checks/deployment-whitelabel.sh.
#
#   GREEN: authored app-profile/ SoT + tokenized deployment/ (no identity literal) → exit 0.
#   RED:   valid app-profile/ but a template identity LITERAL ('mifos-x-web') leaked into a
#          template-owned deployment file (wrangler.toml) → B2 fires → exit 1.
#          (A missing app-profile/app.yaml would likewise fail via B1 — same exit 1 class.)
#
# Exercises the REAL check against each fixture as HEALTH_ROOT. TEMPLATE_SELF_BUILD is unset so
# the self-skip does not mask the assertion. Exit 0 = canary PASS.
set -uo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CHECK="$(cd "$HERE/../.." && pwd)/checks/deployment-whitelabel.sh"

run() {  # <fixture-dir>
  env -u TEMPLATE_SELF_BUILD HEALTH_ROOT="$1" FORK_PROPERTIES="" bash "$CHECK"
}

rc_ok=0
echo "── GREEN ──"
out="$(run "$HERE/green")"; g=$?
printf '%s\n' "$out" | sed 's/^/   /'
if [ "$g" -eq 0 ]; then echo "   ✅ GREEN exit 0 (expected 0)"; else echo "   ❌ GREEN exit $g (expected 0)"; rc_ok=1; fi

echo "── RED ──"
out="$(run "$HERE/red")"; r=$?
printf '%s\n' "$out" | sed 's/^/   /'
if [ "$r" -eq 1 ]; then echo "   ✅ RED exit 1 (expected 1)"; else echo "   ❌ RED exit $r (expected 1)"; rc_ok=1; fi

echo ""
[ "$rc_ok" -eq 0 ] && echo "canary: PASS" || echo "canary: FAIL"
exit "$rc_ok"
