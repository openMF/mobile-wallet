#!/bin/bash
#
# Kotlin Multiplatform Project Customizer
#
# Usage:
#   bash scripts/white-label/customize.sh <package_id> <project_name> [app_display_name] [ios_team_id] [--keep-demo]
#   bash scripts/white-label/customize.sh --clean [--apply]            # standalone demo removal (no identity change)
#
# Example:
#   bash scripts/white-label/customize.sh com.mybank.app MyBankApp "My Bank" ABCDE12345
#   bash scripts/white-label/customize.sh com.mybank.app MyBankApp "My Bank" ABCDE12345 --keep-demo
#
# What this does (identity mode):
#   1. Writes app.id into gradle/fork.properties (its single source of truth) + mirrors the
#      remaining build-time identity (appDisplayName/projectName/iosTeamId) into
#      gradle/libs.versions.toml. syncForkConfig keeps libs.versions.toml#appId synced from app.id.
#   2. Runs ./gradlew syncForkConfig to propagate to iOS Config.xcconfig,
#      local.properties (Fastlane), and gradle.properties (rootProject.name)
#   3. Removes the demo showcase (scripts/remove-demo.sh) so the fork starts from a
#      clean, branded framework shell — this is the DEFAULT. Pass --keep-demo to retain
#      the Money-Toolkit demo (for exploring the framework's reference features).
#
# No source file scanning, no package renaming, no sync-dirs conflicts. The convention plugin
# derives all module namespaces from the framework-owned BASE_MODULE_NAMESPACE constant (kpt) —
# a fixed template label never exposed to the consumer. Fork identity is never touched by the demo removal.
#

set -e

# ── Colors ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
BOLD='\033[1m'
NC='\033[0m'

print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_info()    { echo -e "${BLUE}⚙️  $1${NC}"; }
print_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }
print_error()   { echo -e "${RED}✘ $1${NC}"; exit 1; }

# ── Verify bash version ──────────────────────────────────────────────────────
if [[ ${BASH_VERSINFO[0]} -lt 4 ]]; then
  print_error "Bash 4+ required. macOS ships with Bash 3 — run: brew install bash"
fi

# ── --clean: strip the demo showcase (showcase-framework-separation) ─────────
# Delegates to scripts/remove-demo.sh. Dry-run by default; pass --apply to perform.
# Removal only — does NOT read or write the app-namespace identity key (out of scope).
if [[ "${1:-}" == "--clean" ]]; then
  shift
  print_info "Demo-showcase removal (customizer --clean)…"
  bash "$(dirname "$0")/scripts/remove-demo.sh" "$@"
  exit $?
fi

# ── Flags (extracted before positional parsing so they can appear anywhere) ──
# Identity mode removes the demo showcase BY DEFAULT (the point of the separation:
# forking = starting clean). --keep-demo retains it; --no-format forwards to the strip.
KEEP_DEMO=0
STRIP_FORMAT_FLAG=""
POSITIONAL=()
for a in "$@"; do
  case "$a" in
    --keep-demo) KEEP_DEMO=1 ;;
    --no-format) STRIP_FORMAT_FLAG="--no-format" ;;
    *)           POSITIONAL+=("$a") ;;
  esac
done
set -- "${POSITIONAL[@]+"${POSITIONAL[@]}"}"

# ── Args ─────────────────────────────────────────────────────────────────────
if [[ $# -lt 2 ]]; then
  echo -e "${BOLD}Usage:${NC} bash scripts/white-label/customize.sh <package_id> <project_name> [app_display_name] [ios_team_id]"
  echo
  echo -e "${BOLD}Examples:${NC}"
  echo "  bash scripts/white-label/customize.sh com.mybank.app MyBankApp"
  echo "  bash scripts/white-label/customize.sh com.mybank.app MyBankApp \"My Bank\" ABCDE12345"
  exit 2
fi

PACKAGE=$1
PROJECT_NAME=$2
APPNAME=${3:-$PROJECT_NAME}
TEAM_ID=${4:-"XXXXXXXXXX"}

# NOTE: module Android namespaces (kpt.*) are a FRAMEWORK-owned label, fixed in build-logic
# (org.convention.BASE_MODULE_NAMESPACE) — NOT exposed to the consumer, so nothing to derive/write here.

LIBS_TOML="gradle/libs.versions.toml"

if [[ ! -f "$LIBS_TOML" ]]; then
  print_error "$LIBS_TOML not found. Run this script from the project root."
fi

echo
echo -e "${BLUE}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║        Kotlin Multiplatform Project Customizer       ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════╝${NC}"
echo
print_info "Package ID:       $PACKAGE"
print_info "Project name:     $PROJECT_NAME"
print_info "Display name:     $APPNAME"
print_info "iOS Team ID:      $TEAM_ID"
echo

# ── Update libs.versions.toml ────────────────────────────────────────────────
print_info "Updating $LIBS_TOML..."

sed -i.bak "s|appId[[:space:]]*=.*|appId            = \"$PACKAGE\"|"             "$LIBS_TOML"
sed -i.bak "s|appDisplayName[[:space:]]*=.*|appDisplayName   = \"$APPNAME\"|"    "$LIBS_TOML"
sed -i.bak "s|projectName[[:space:]]*=.*|projectName      = \"$PROJECT_NAME\"|"  "$LIBS_TOML"
sed -i.bak "s|iosTeamId[[:space:]]*=.*|iosTeamId        = \"$TEAM_ID\"|"         "$LIBS_TOML"
find . -name "*.bak" -not -path "*/build/*" -delete

print_success "libs.versions.toml updated"

# ── Author app.id into gradle/fork.properties (its single source of truth) ────
# app.id is authored in fork.properties; syncForkConfig writes it back into libs.versions.toml.
# We set BOTH here so there is never a drift window even if syncForkConfig is skipped.
FORK_PROPS="gradle/fork.properties"
[[ -f "$FORK_PROPS" ]] || { cp gradle/fork.properties.template "$FORK_PROPS" 2>/dev/null && print_info "Created $FORK_PROPS from template"; }
if [[ -f "$FORK_PROPS" ]]; then
  if grep -qE '^app\.id=' "$FORK_PROPS"; then
    sed -i.bak "s|^app\.id=.*|app.id=$PACKAGE|" "$FORK_PROPS" && rm -f "$FORK_PROPS.bak"
  else
    printf '\napp.id=%s\n' "$PACKAGE" >> "$FORK_PROPS"
  fi
  print_success "fork.properties app.id=$PACKAGE (source of truth)"
fi

# ── Regenerate all platform config files ─────────────────────────────────────
print_info "Running ./gradlew syncForkConfig..."
if ./gradlew syncForkConfig --quiet; then
  print_success "iOS Config.xcconfig regenerated"
  print_success "local.properties updated (Fastlane)"
  print_success "gradle.properties updated (rootProject.name)"
else
  print_warning "syncForkConfig failed — you may need to run it manually after Gradle syncs."
fi

# ── Remove the demo showcase (DEFAULT — forking = clean start) ────────────────
if [[ "$KEEP_DEMO" -eq 0 ]]; then
  echo
  print_info "Removing demo showcase (default — pass --keep-demo to retain it)…"
  if bash "$(dirname "$0")/scripts/remove-demo.sh" --apply --all $STRIP_FORMAT_FLAG; then
    print_success "Demo showcase removed — clean, branded framework shell ready"
  else
    print_warning "Demo removal reported an issue — review the scripts/remove-demo.sh output above."
  fi
else
  echo
  print_info "Keeping demo showcase (--keep-demo)."
fi

echo
# ── Project health — surface anything still template-default ──────────────────
# fork.properties is the project-level source of truth; run the sanity harness so the fork
# immediately sees what customization left un-forked (signing/org identity, store copy).
# Non-fatal here — CI's quality-gate is the hard gate; this is guidance right after forking.
if [[ -f "$(dirname "$0")/../product-health/product-health.sh" ]]; then
  print_info "Running product health check (gradle/fork.properties sanity)…"
  bash "$(dirname "$0")/../product-health/product-health.sh" \
    || print_warning "Project health flagged items above — set them in gradle/fork.properties before releasing."
  echo
fi

echo -e "${GREEN}${BOLD}✨ Customization complete!${NC}"
echo
echo -e "${YELLOW}Next steps:${NC}"
echo "  1. Replace cmp-android/google-services.json with your Firebase config"
echo "  2. Update fastlane-config/project_config.rb Firebase App IDs"
echo "  3. Replace cmp-ios/iosApp/Assets.xcassets with your app icon"
echo "  4. Run ./gradlew build to verify everything compiles"
echo
echo -e "  To update identity later: edit ${BOLD}gradle/fork.properties${NC} → run ${BOLD}./gradlew syncForkConfig${NC}"
