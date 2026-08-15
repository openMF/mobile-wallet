#!/usr/bin/env bash
# checks/appid-consistency.sh — gradle/fork.properties#app.id is the AUTHORED single source of truth
# for the app bundle id; gradle/libs.versions.toml#appId is a DERIVED surface (the whole build reads
# it via libs.versions.appId, and ./gradlew syncForkConfig writes it back from fork.properties).
#
# This check FAILs if the two drift — which means a fork edited app.id in fork.properties but did not
# run ./gradlew syncForkConfig, so the build would still compile with the old bundle id. exit 0 PASS /
# 1 FAIL. Runs on the template too (the invariant holds everywhere: they must agree).
set -uo pipefail
# shellcheck source=scripts/product-health/lib.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/lib.sh"
: "${FORK_PROPERTIES:?appid-consistency: FORK_PROPERTIES not set (run via product-health.sh)}"
: "${HEALTH_ROOT:?appid-consistency: HEALTH_ROOT not set (run via product-health.sh)}"

# gradle/fork.properties is gitignored (per-fork), so on CI / a fresh clone health_resolve_sot falls
# back to gradle/fork.properties.template — a placeholder (app.id=com.yourcompany.yourapp), NOT a
# materialized fork config. There is nothing to cross-check: the catalog is the sole appId source and
# fork-identity.sh already guards it. PASS. (The invariant is only meaningful for a real fork.properties.)
case "${FORK_PROPERTIES}" in
  *fork.properties.template)
    echo "fork.properties not materialized (using .template placeholder) — catalog is the sole appId source (ok)"
    exit 0 ;;
esac

fp_appid="$(fp_get app.id)"
# No app.id authored in fork.properties → this fork hasn't consolidated appId yet; the catalog is the
# sole source and fork-identity.sh already guards it. Nothing to cross-check. PASS.
[ -n "$fp_appid" ] || { echo "no app.id in fork.properties — catalog is the sole appId source (ok)"; exit 0; }

toml="$HEALTH_ROOT/gradle/libs.versions.toml"
toml_appid="$(grep -E '^\s*appId\s*=' "$toml" 2>/dev/null | head -1 | sed 's/.*=[[:space:]]*//; s/[[:space:]]*#.*$//; s/"//g; s/[[:space:]]*$//')"

if [ "$fp_appid" != "$toml_appid" ]; then
  echo "❌ appId drift: fork.properties app.id='$fp_appid' ≠ libs.versions.toml appId='$toml_appid'"
  echo "→ Fix: run ./gradlew syncForkConfig — fork.properties is the SoT; the version catalog is derived."
  exit 1
fi
echo "app.id consistent: fork.properties + libs.versions.toml both '$fp_appid'"
exit 0
