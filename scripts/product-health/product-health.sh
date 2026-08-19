#!/usr/bin/env bash
# scripts/product-health/product-health.sh — product health / sanity harness.
#
# Reads gradle/fork.properties (the project-level SINGLE SOURCE OF TRUTH) and runs every
# scripts/product-health/checks/*.sh against it. This is the ONE place a fork learns whether its
# customization is complete + correct: signing/org identity re-forked, appId consolidated, store
# listing authored. Runs in CI (quality-gate.yml) and at the end of scripts/white-label/customize.sh so a fresh fork
# gets an immediate sanity report. Pure bash — no Gradle, no network.
#
# Check contract (each checks/*.sh): exit 0 = PASS · exit 1 = FAIL (blocks) · exit 2 = WARN
# (needs attention, non-blocking). Fork-only checks self-skip when TEMPLATE_SELF_BUILD=1.
#
# product-health exit: 0 when no check FAILs (WARNs allowed) · 1 when any check FAILs.
set -uo pipefail

HEALTH_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"   # …/scripts/product-health
HEALTH_ROOT="$(cd "$HEALTH_DIR/../.." && pwd)"               # repo root
# shellcheck source=scripts/product-health/lib.sh
source "$HEALTH_DIR/lib.sh"

FORK_PROPERTIES="$(health_resolve_sot "$HEALTH_ROOT")" || {
  echo "${C_RED}❌ project-health: no gradle/fork.properties(.template) found — is this a fork of kmp-project-template?${C_RST}" >&2
  exit 1
}
export FORK_PROPERTIES HEALTH_ROOT

# ── Header — name the project from its SoT ───────────────────────────────────
org="$(fp_get org.name)"; [ -n "$org" ] || org="(unset)"
app_id="$(fp_get app.id)"
[ -n "$app_id" ] || app_id="$(grep -E '^\s*appId\s*=' "$HEALTH_ROOT/gradle/libs.versions.toml" 2>/dev/null | head -1 | sed 's/.*=[[:space:]]*//; s/[[:space:]]*#.*$//; s/"//g; s/[[:space:]]*$//')"
[ -n "$app_id" ] || app_id="?"
echo "── ${C_BLD}Product Health${C_RST} · ${org} ${C_DIM}(${app_id})${C_RST} ──"
echo "   ${C_DIM}SoT: ${FORK_PROPERTIES#$HEALTH_ROOT/}${C_RST}"
[ "${TEMPLATE_SELF_BUILD:-}" = "1" ] && echo "   ${C_DIM}TEMPLATE_SELF_BUILD=1 — fork-only checks skip (this IS the upstream template)${C_RST}"
echo ""

# ── Run every check ──────────────────────────────────────────────────────────
fail=0; warn=0; pass=0
for chk in "$HEALTH_DIR"/checks/*.sh; do
  [ -f "$chk" ] || continue
  name="$(basename "$chk" .sh)"
  out="$(bash "$chk" 2>&1)"; rc=$?
  case "$rc" in
    0) pass=$((pass+1)); printf '  %s✅ PASS%s  %s\n' "$C_GRN" "$C_RST" "$name" ;;
    2) warn=$((warn+1)); printf '  %s⚠️  WARN%s  %s\n' "$C_YEL" "$C_RST" "$name" ;;
    *) fail=$((fail+1)); printf '  %s❌ FAIL%s  %s\n' "$C_RED" "$C_RST" "$name" ;;
  esac
  # Show the check's own lines (indented) whenever it wasn't a clean pass.
  [ "$rc" -ne 0 ] && [ -n "$out" ] && printf '%s\n' "$out" | sed 's/^/         /'
done

echo ""
echo "── ${pass} passed · ${warn} warn · ${fail} failed ──"
[ "$warn" -gt 0 ] && [ "$fail" = 0 ] && echo "   ${C_DIM}(warnings don't block — but resolve them before releasing)${C_RST}"
[ "$fail" -gt 0 ] && exit 1
exit 0
