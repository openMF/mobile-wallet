#!/usr/bin/env bash
# scripts/product-health/lib.sh — shared helpers for the product-health harness.
#
# gradle/fork.properties is the SINGLE SOURCE OF TRUTH for project-level data (identity,
# signing, testers, firebase, store listing, legal). EVERY health check reads it through
# fp_get() so there is exactly one parser and one SoT — a check never re-implements the
# read. Sourced by project-health.sh and each checks/*.sh; never executed directly.

# health_resolve_sot <repo_root> — echo the path to the project SoT, or return 1.
# Honors a caller-provided $FORK_PROPERTIES (lets a test point at a fixture).
health_resolve_sot() {
  local root="$1"
  if [ -n "${FORK_PROPERTIES:-}" ] && [ -f "$FORK_PROPERTIES" ]; then echo "$FORK_PROPERTIES"; return 0; fi
  if [ -f "$root/gradle/fork.properties" ]; then echo "$root/gradle/fork.properties"; return 0; fi
  if [ -f "$root/gradle/fork.properties.template" ]; then echo "$root/gradle/fork.properties.template"; return 0; fi
  return 1
}

# fp_get <key> — read one key from $FORK_PROPERTIES, trimming inline `# comment` + whitespace.
fp_get() {
  [ -f "${FORK_PROPERTIES:-}" ] || return 1
  grep -E "^$1=" "$FORK_PROPERTIES" 2>/dev/null | head -1 | cut -d= -f2- | sed 's/[[:space:]]*#.*$//; s/[[:space:]]*$//'
}

# Colors — only when stdout is a tty.
if [ -t 1 ]; then
  C_RED=$'\033[0;31m'; C_GRN=$'\033[0;32m'; C_YEL=$'\033[0;33m'; C_DIM=$'\033[2m'; C_BLD=$'\033[1m'; C_RST=$'\033[0m'
else
  C_RED=; C_GRN=; C_YEL=; C_DIM=; C_BLD=; C_RST=
fi

# ── White-label placeholder vocabulary (WHITE_LABEL_PLACEHOLDERS.yaml) ─────────
# The neutral upstream template ships every identity/brand field as a DECLARED
# placeholder. The three identity checks (fork-identity / deployment-whitelabel /
# store-listing) load that single-source vocabulary through wl_placeholders_load so
# they are directional + template-aware (pure-white-label-100 WS6, RULE-TEMPLATE-
# MODULE-FIX-UPSTREAM-001-class generalization). Pure bash + grep + awk — no jq.
: "${WL_PLACEHOLDERS_YAML:=}"
if [ -z "$WL_PLACEHOLDERS_YAML" ]; then
  # 1. Template-local vendored copy — next to this lib. This is what a STANDALONE template
  #    checkout (CI) and every FORK resolve, since neither has the framework monorepo on disk.
  _self_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  if [ -f "$_self_dir/WHITE_LABEL_PLACEHOLDERS.yaml" ]; then
    WL_PLACEHOLDERS_YAML="$_self_dir/WHITE_LABEL_PLACEHOLDERS.yaml"
  else
    # 2. Dev fallback: walk up from HEALTH_ROOT to the framework's core/registries copy (monorepo).
    _d="${HEALTH_ROOT:-$(pwd)}"
    while [ "$_d" != "/" ] && [ -n "$_d" ]; do
      if [ -f "$_d/core/registries/WHITE_LABEL_PLACEHOLDERS.yaml" ]; then
        WL_PLACEHOLDERS_YAML="$_d/core/registries/WHITE_LABEL_PLACEHOLDERS.yaml"; break
      fi
      _d="$(dirname "$_d")"
    done
  fi
fi
export WL_PLACEHOLDERS_YAML

# wl_placeholders_load <category> — emit one regex pattern per line under placeholders.<category>.
# The reset guard (any top-level `^key:` other than placeholders: closes the section) keeps this
# robust when a sibling top-level block (e.g. example_identity:) follows placeholders: in the file.
wl_placeholders_load() {
  local cat="$1"
  [ -f "$WL_PLACEHOLDERS_YAML" ] || { echo "wl_placeholders_load: WHITE_LABEL_PLACEHOLDERS.yaml not found" >&2; return 2; }
  awk -v cat="$cat" '
    /^placeholders:/   { in_ph=1; next }
    /^[a-zA-Z_]+:/ && !/^placeholders:/ { in_ph=0 }
    in_ph && /^  [a-z_]+:[[:space:]]*$/ { curr=$1; sub(/:$/,"",curr); next }
    in_ph && curr==cat && /^    - / {
      val=$0; sub(/^    - /,"",val)
      gsub(/^["'\''"]/,"",val); gsub(/["'\''"]$/,"",val)
      print val
    }
  ' "$WL_PLACEHOLDERS_YAML"
}

# wl_matches_any <value> <category> — return 0 if value matches ANY pattern under category, else 1.
wl_matches_any() {
  local val="$1" cat="$2" pat
  while IFS= read -r pat; do
    [ -n "$pat" ] || continue
    printf '%s' "$val" | grep -qE "$pat" && return 0
  done < <(wl_placeholders_load "$cat")
  return 1
}

# wl_example_load <category> — emit one regex per line under example_identity.<category>.
# example_identity is the template's COMMITTED reference/demo identity (Mifos "Money Toolkit"):
# template-mode gates ACCEPT it; fork-mode gates FAIL it (a fork must diverge from the reference).
# A category with NO example block (e.g. signing: apple_team_id / apple_match_git_url) emits nothing
# → wl_matches_example is always false for it → it stays placeholder-only (signing is never committed).
wl_example_load() {
  local cat="$1"
  [ -f "$WL_PLACEHOLDERS_YAML" ] || { echo "wl_example_load: WHITE_LABEL_PLACEHOLDERS.yaml not found" >&2; return 2; }
  awk -v cat="$cat" '
    /^example_identity:/ { in_ex=1; next }
    /^[a-zA-Z_]+:/ && !/^example_identity:/ { in_ex=0 }
    in_ex && /^  [a-z_]+:[[:space:]]*$/ { curr=$1; sub(/:$/,"",curr); next }
    in_ex && curr==cat && /^    - / {
      val=$0; sub(/^    - /,"",val)
      gsub(/^["'\''"]/,"",val); gsub(/["'\''"]$/,"",val)
      print val
    }
  ' "$WL_PLACEHOLDERS_YAML"
}

# wl_matches_example <value> <category> — return 0 if value IS the declared reference identity.
wl_matches_example() {
  local val="$1" cat="$2" pat
  while IFS= read -r pat; do
    [ -n "$pat" ] || continue
    printf '%s' "$val" | grep -qE "$pat" && return 0
  done < <(wl_example_load "$cat")
  return 1
}

# Guard against direct execution — this is a library.
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
  echo "lib.sh is a library — source it, don't run it." >&2; exit 64
fi
