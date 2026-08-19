#!/usr/bin/env bash
# checks/fork-identity.sh — DIRECTIONAL, placeholder-aware signing + org identity guard.
#
# A fork must author its OWN signing + org identity (apple.team.id / apple.match.git.url /
# org.name / appId); the NEUTRAL template must ship those fields as DECLARED placeholders. This
# check loads the single-source placeholder vocabulary (core/registries/WHITE_LABEL_PLACEHOLDERS.yaml,
# resolved by lib.sh) and enforces the direction — replacing the old blanket TEMPLATE_SELF_BUILD=1
# skip (which was a false negative in BOTH directions: it hid a real-brand leak on the template AND
# an unfilled placeholder on a fork). pure-white-label-100 WS6 — RULE-TEMPLATE-MODULE-FIX-UPSTREAM-001
# identity/brand-field generalization.
#
#   template (TEMPLATE_SELF_BUILD=1) → EVERY field MUST match a declared placeholder (leak → FAIL)
#   fork     (TEMPLATE_SELF_BUILD unset) → NO field may match a placeholder (unfilled → FAIL)
#
# exit 0 PASS / 1 FAIL.
set -uo pipefail
# shellcheck source=scripts/product-health/lib.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/lib.sh"
: "${FORK_PROPERTIES:?fork-identity: FORK_PROPERTIES not set (run via product-health.sh)}"
: "${HEALTH_ROOT:?fork-identity: HEALTH_ROOT not set (run via product-health.sh)}"

# Signing/org fields checked, mapped to their WHITE_LABEL_PLACEHOLDERS.yaml category.
declare -A FIELD_CAT=(
  [apple.team.id]=apple_team_id
  [apple.match.git.url]=apple_match_git_url
  [org.name]=org_name
)

fail=0
is_template="${TEMPLATE_SELF_BUILD:-0}"
for key in "${!FIELD_CAT[@]}"; do
  cat="${FIELD_CAT[$key]}"
  val="$(fp_get "$key")"
  if wl_matches_any "$val" "$cat"; then
    # value IS a declared placeholder
    if [ "$is_template" != "1" ]; then
      echo "❌ '$key=$val' is a template placeholder — a fork must set its own value (see WHITE_LABEL_PLACEHOLDERS.yaml#$cat)"
      fail=1
    fi
  else
    # value is authored
    if [ "$is_template" = "1" ]; then
      echo "❌ '$key=$val' is authored on the NEUTRAL TEMPLATE — a real-brand value leaked in (must be a placeholder from WHITE_LABEL_PLACEHOLDERS.yaml#$cat)"
      fail=1
    fi
  fi
done

# Bundle id in libs.versions.toml — same directional model (WHITE_LABEL_PLACEHOLDERS.yaml#bundle_id).
lvt="$HEALTH_ROOT/gradle/libs.versions.toml"
if [ -f "$lvt" ]; then
  bid="$(grep -E '^[[:space:]]*appId[[:space:]]*=' "$lvt" | head -1 | sed -E 's/.*=[[:space:]]*"?([^"]+)"?.*/\1/')"
  if wl_matches_any "$bid" bundle_id; then
    [ "$is_template" != "1" ] && { echo "❌ libs.versions.toml appId '$bid' is a template placeholder — fork must author"; fail=1; }
  else
    [ "$is_template" = "1" ] && { echo "❌ libs.versions.toml appId '$bid' is authored on the NEUTRAL TEMPLATE — must be a placeholder"; fail=1; }
  fi

  # Apple Team ID in libs.versions.toml — same directional model (WHITE_LABEL_PLACEHOLDERS.yaml#apple_team_id).
  # A real Apple Team ID literal (e.g. "L432S2FZP5") in the neutral catalog is a signing-identity leak; a
  # fork must author its own. syncForkConfig resolves iosTeamId app-profile-first, so the catalog is a
  # fallback that MUST stay a placeholder on the template.
  # An ABSENT iosTeamId line is neither a leak nor an unfilled placeholder — it means the value is resolved
  # app-profile-first with no catalog fallback authored — so run the directional check ONLY when the line
  # is present (an empty match token would otherwise be miscategorized as a "real" value → false leak).
  tid_line="$(grep -E '^[[:space:]]*iosTeamId[[:space:]]*=' "$lvt" | head -1)"
  if [ -n "$tid_line" ]; then
    tid="$(printf '%s' "$tid_line" | sed -E 's/.*=[[:space:]]*"?([^"]+)"?.*/\1/')"
    if wl_matches_any "$tid" apple_team_id; then
      [ "$is_template" != "1" ] && { echo "❌ libs.versions.toml iosTeamId '$tid' is a template placeholder — fork must author its own Apple Team ID"; fail=1; }
    else
      [ "$is_template" = "1" ] && { echo "❌ libs.versions.toml iosTeamId '$tid' is a REAL Apple Team ID authored on the NEUTRAL TEMPLATE — signing-identity leak (must be the YOUR_TEAM_ID placeholder from WHITE_LABEL_PLACEHOLDERS.yaml#apple_team_id)"; fail=1; }
    fi
  fi
fi

if [ "$fail" = 0 ]; then
  echo "gradle/fork.properties + libs.versions.toml directional identity OK (template-aware via WHITE_LABEL_PLACEHOLDERS.yaml)"
  exit 0
fi
echo "→ Fix: author gradle/fork.properties (apple.team.id, apple.match.git.url, org.name) + libs.versions.toml appId for your fork."
exit 1
