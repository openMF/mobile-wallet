#!/usr/bin/env bash
# checks/ios-pbxproj-identity.sh — the iOS Xcode project MUST derive its bundle id + provisioning
# profile from Config.xcconfig (which `./gradlew syncForkConfig` writes APP_BUNDLE_ID into), NEVER
# hardcode identity inside cmp-ios/iosApp.xcodeproj/project.pbxproj.
#
# Why this class breaks forks (xcodebuild Exit status: 65):
#   `fastlane gym` enumerates EVERY build configuration in the project to assemble the
#   export_options.provisioningProfiles map — including dead/vestigial base Debug/Release configs.
#   A config that hardcodes a bundle id (the template's own `org.mifos.kmp.template`, or a stale
#   fork id) or a hardcoded `PROVISIONING_PROFILE_SPECIFIER = "match AppStore <id>"` makes gym sign
#   against a profile that was never minted for THIS fork's distribution cert →
#   "Provisioning profile … doesn't include signing certificate …". syncForkConfig only writes
#   Config.xcconfig; it never touches project.pbxproj, so a hardcoded id survives every fork.
#
# Invariant (holds on the template AND every fork — no TEMPLATE_SELF_BUILD skip):
#   PBX-1  project.pbxproj contains NO literal `org.mifos.kmp.template` (identity lives in
#          Config.xcconfig / libs.versions.toml#appId, never in the Xcode project).
#   PBX-2  every PRODUCT_BUNDLE_IDENTIFIER value ∈ { "$(APP_BUNDLE_ID)", "$(inherited)" }.
#   PBX-3  every PROVISIONING_PROFILE_SPECIFIER is empty "" OR references $(APP_BUNDLE_ID) —
#          never a hardcoded profile name (let match / automatic signing resolve it).
#
# exit 0 = PASS · 1 = FAIL (blocks customize + CI). No cmp-ios project → PASS (nothing to guard).
set -uo pipefail
# shellcheck source=scripts/product-health/lib.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/lib.sh"
: "${HEALTH_ROOT:?ios-pbxproj-identity: HEALTH_ROOT not set (run via product-health.sh)}"

PBX="$HEALTH_ROOT/cmp-ios/iosApp.xcodeproj/project.pbxproj"
[ -f "$PBX" ] || { echo "no cmp-ios Xcode project — nothing to guard (ok)"; exit 0; }

fail=0

# PBX-1 — no literal template bundle id anywhere in the Xcode project.
if grep -q 'org\.mifos\.kmp\.template' "$PBX"; then
  echo "${C_RED}✗ PBX-1${C_RST}: project.pbxproj hardcodes 'org.mifos.kmp.template' — identity must derive from Config.xcconfig (\$(APP_BUNDLE_ID)):"
  grep -nE 'org\.mifos\.kmp\.template' "$PBX" | sed 's/^/       /'
  fail=1
fi

# PBX-2 — PRODUCT_BUNDLE_IDENTIFIER must be a derived token, never a literal id.
bad_bid="$(grep -oE 'PRODUCT_BUNDLE_IDENTIFIER = "[^"]*"' "$PBX" \
  | grep -vE 'PRODUCT_BUNDLE_IDENTIFIER = "\$\((APP_BUNDLE_ID|inherited)\)"' || true)"
if [ -n "$bad_bid" ]; then
  echo "${C_RED}✗ PBX-2${C_RST}: PRODUCT_BUNDLE_IDENTIFIER must be \"\$(APP_BUNDLE_ID)\" or \"\$(inherited)\", found:"
  printf '%s\n' "$bad_bid" | sort -u | sed 's/^/       /'
  fail=1
fi

# PBX-3 — PROVISIONING_PROFILE_SPECIFIER: empty, or references $(APP_BUNDLE_ID); never hardcoded.
bad_prof="$(grep -oE 'PROVISIONING_PROFILE_SPECIFIER = "[^"]*"' "$PBX" \
  | grep -vE 'PROVISIONING_PROFILE_SPECIFIER = ""|PROVISIONING_PROFILE_SPECIFIER = "[^"]*\$\(APP_BUNDLE_ID\)[^"]*"' || true)"
if [ -n "$bad_prof" ]; then
  echo "${C_RED}✗ PBX-3${C_RST}: PROVISIONING_PROFILE_SPECIFIER must be empty or \$(APP_BUNDLE_ID)-derived, found hardcoded:"
  printf '%s\n' "$bad_prof" | sort -u | sed 's/^/       /'
  fail=1
fi

if [ "$fail" = 0 ]; then
  echo "${C_GRN}✓${C_RST} iOS project derives bundle id + provisioning from Config.xcconfig (no hardcoded identity)"
  exit 0
fi
echo "  ↳ fix: on the offending XCBuildConfiguration(s) in cmp-ios/iosApp.xcodeproj/project.pbxproj set"
echo "         PRODUCT_BUNDLE_IDENTIFIER = \"\$(APP_BUNDLE_ID)\"  and  PROVISIONING_PROFILE_SPECIFIER = \"\"."
exit 1
