#!/usr/bin/env bash
# checks/secrets-alias-namespace.sh — DIRECTIONAL guard on the vault-facing secrets files
# (secrets-manifest.yaml + secrets/LAYOUT.yaml) so no real org identity is committed as a LITERAL
# in the brand-neutral template, and no unfilled placeholder ships in a fork.
#
# These two files carry fork identity: (1) the Fastlane Match provisioning-profile git URL and
# (2) the vault-alias PREFIXES. syncForkConfig (SyncForkConfigPlugin.kt §6g) DERIVES both from
# app-profile — the match URL from app-profile#apple.match.git.url and the alias prefixes from the
# project slug — so a vault-mode (Path-B) fork rebrands with ZERO hand-edits.
#
# This check enforces the ONE part that is a hard leak either direction: the ios-provisioning-profile
# git URL literal. It reuses the single-source vocabulary WHITE_LABEL_PLACEHOLDERS.yaml#apple_match_git_url
# (resolved by lib.sh), mirroring fork-identity.sh exactly:
#
#   template (TEMPLATE_SELF_BUILD=1) → the match URL MUST be a declared placeholder (a real
#                                      org URL such as openMF/… → FAIL, signing-identity leak)
#   fork     (TEMPLATE_SELF_BUILD unset) → the match URL must NOT be a placeholder (unfilled → FAIL)
#
# The `mifos-x-` alias prefix is NOT flagged here: on the upstream template it is the template's own
# legitimate, vault-resolvable org namespace (registered in the framework SECRETS_ALIAS_REGISTRY), and
# syncForkConfig rewrites it to the fork's namespace only on a genuine fork. It is fork identity, not a
# leak, so guarding it would fail the template's legitimate committed state.
#
# exit 0 PASS / 1 FAIL.
set -uo pipefail
# shellcheck source=scripts/product-health/lib.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/lib.sh"
: "${HEALTH_ROOT:?secrets-alias-namespace: HEALTH_ROOT not set (run via product-health.sh)}"

fail=0
is_template="${TEMPLATE_SELF_BUILD:-0}"

# Files that may carry the provisioning-profile git URL literal.
for rel in secrets/LAYOUT.yaml secrets-manifest.yaml; do
  f="$HEALTH_ROOT/$rel"
  [ -f "$f" ] || continue
  # Extract every ios-provisioning-profile git URL literal in the file (comments stripped).
  while IFS= read -r url; do
    [ -n "$url" ] || continue
    if wl_matches_any "$url" apple_match_git_url; then
      # value IS a declared placeholder
      if [ "$is_template" != "1" ]; then
        echo "❌ $rel: match git URL '$url' is a template placeholder — a fork must author apple.match.git.url in app-profile (WHITE_LABEL_PLACEHOLDERS.yaml#apple_match_git_url)"
        fail=1
      fi
    else
      # value is an authored real-org URL
      if [ "$is_template" = "1" ]; then
        echo "❌ $rel: match git URL '$url' is a REAL org literal on the NEUTRAL TEMPLATE — leak (must be the YOUR_ORG placeholder; syncForkConfig derives it from app-profile#apple.match.git.url)"
        fail=1
      fi
    fi
  done < <(sed 's/#.*$//' "$f" | grep -oE 'git@github\.com:[^"'"'"' ]+/ios-provisioning-profile\.git')
done

if [ "$fail" = 0 ]; then
  echo "secrets alias/URL namespace OK (provisioning-profile URL directional via WHITE_LABEL_PLACEHOLDERS.yaml; alias prefixes derived by syncForkConfig)"
  exit 0
fi
echo "→ Fix: author apple.match.git.url in app-profile/platforms/apple/apple.yaml + run ./gradlew syncForkConfig (never hand-write an org literal in secrets/LAYOUT.yaml)."
exit 1
