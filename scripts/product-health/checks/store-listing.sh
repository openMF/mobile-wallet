#!/usr/bin/env bash
# checks/store-listing.sh — DIRECTIONAL, placeholder-aware store-copy guard.
#
# fork-identity.sh covers the SIGNING/org fields (Apple rejects a wrong team). This is the softer
# twin: the store *content* (title, subtitle, description, promo, copyright) that ships to the App
# Store / Play listing. It loads the single-source placeholder vocabulary
# (core/registries/WHITE_LABEL_PLACEHOLDERS.yaml, resolved by lib.sh) — replacing the old hardcoded
# TEMPLATE_MARKER regex and the blanket TEMPLATE_SELF_BUILD=1 skip (pure-white-label-100 WS6).
#
#   template (TEMPLATE_SELF_BUILD=1) → every store-copy key MUST match a placeholder marker; an
#                                      authored value is a real-brand leak → FAIL (exit 1).
#   fork     (TEMPLATE_SELF_BUILD unset) → NO key may still match a placeholder marker; WARN (exit 2),
#                                      since prose is human-authored and must not ship-block a build.
#
# exit 0 PASS / 1 FAIL (template leak) / 2 WARN (fork unfilled).
set -uo pipefail
# shellcheck source=scripts/product-health/lib.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/lib.sh"
: "${FORK_PROPERTIES:?store-listing: FORK_PROPERTIES not set (run via product-health.sh)}"

is_template="${TEMPLATE_SELF_BUILD:-0}"
# Union placeholder regex across the three categories that describe "template store copy".
MARKER_RE="$(
  {
    wl_placeholders_load app_display_name
    wl_placeholders_load org_name
    wl_placeholders_load store_copy_marker
  } | paste -sd'|' -
)"
KEYS=(store.title store.subtitle store.description store.promotional.text app.description store.copyright)

leaked=(); authored=()
for key in "${KEYS[@]}"; do
  val="$(fp_get "$key")"
  if [ -n "$MARKER_RE" ] && echo "$val" | grep -qiE "$MARKER_RE"; then
    leaked+=("$key")
  else
    authored+=("$key")
  fi
done

if [ "$is_template" = "1" ]; then
  # template: every key MUST match a placeholder marker; an authored value = real-brand leak = FAIL.
  if [ "${#authored[@]}" -ne 0 ]; then
    echo "❌ store copy on NEUTRAL TEMPLATE has AUTHORED value(s) — must remain placeholder(s) per WHITE_LABEL_PLACEHOLDERS.yaml:"
    for k in "${authored[@]}"; do echo "     · $k"; done
    exit 1
  fi
  echo "template store copy is all placeholder markers (WHITE_LABEL_PLACEHOLDERS.yaml directional PASS)"
  exit 0
fi

# fork: NO key may match a placeholder marker; WARN (exit 2), since prose is human-authored.
if [ "${#leaked[@]}" -ne 0 ]; then
  echo "⚠️  store copy still carries template placeholder(s) — author for your app in gradle/fork.properties (WHITE_LABEL_PLACEHOLDERS.yaml):"
  for k in "${leaked[@]}"; do echo "     · $k"; done
  echo "   (non-blocking: the listing ships to App Store / Play — write your own before you release.)"
  exit 2
fi
echo "store listing is authored for this fork"
exit 0
