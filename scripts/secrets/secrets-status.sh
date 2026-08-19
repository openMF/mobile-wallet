#!/usr/bin/env bash
# =============================================================================
# scripts/secrets/secrets-status.sh — what's set up vs missing, per platform
# =============================================================================
# Reads deployment/<platform>/*/secrets-needs.yaml and checks each declared
# secret's canonical path on disk. Pure bash — works in a fresh OSS fork.
#
#   scripts/secrets/secrets-status.sh            # all platforms
#   scripts/secrets/secrets-status.sh apple      # one platform
#
# Exit 0 = all present · 1 = some missing. (CI-friendly.)
# =============================================================================
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"; . "$DIR/_lib.sh"

platforms="${1:-all}"; [ "$platforms" = all ] && platforms="$SECRETS_PLATFORMS"
total=0; present=0; missing=0

for p in $platforms; do
  secrets_platform_dir "$p" >/dev/null 2>&1 || { echo "${C_R}unknown platform: $p${C_0} (expected: $SECRETS_PLATFORMS)"; exit 2; }
  echo "${C_B}── ${p} ──────────────────────────────────────────────${C_0}"
  any=0
  while IFS=$'\t' read -r canon hint gha ph tgts; do
    [ -n "$canon" ] || continue
    any=1; total=$((total+1))
    if secrets_canonical_present "$canon"; then
      printf '  %s✓%s %-52s %s%s%s\n' "$C_G" "$C_0" "$canon" "$C_D" "${tgts}" "$C_0"; present=$((present+1))
    else
      printf '  %s✗%s %-52s %s%s%s\n' "$C_R" "$C_0" "$canon" "$C_D" "${gha:-$tgts}" "$C_0"; missing=$((missing+1))
    fi
  done < <(secrets_platform_inputs "$p")
  [ "$any" = 0 ] && echo "  ${C_D}(no manual_inputs declared)${C_0}"
  echo
done

echo "${C_B}Σ ${present}/${total} present · ${missing} missing${C_0}"
[ "$missing" = 0 ] && { echo "${C_G}All declared secrets are in place.${C_0}"; exit 0; }
echo "${C_Y}Run:${C_0} scripts/secrets/setup-secrets.sh <platform>   ${C_D}(or, with the framework: /secrets-source-map)${C_0}"
exit 1
