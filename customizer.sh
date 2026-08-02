#!/bin/bash
#
# Kotlin Multiplatform Project Customizer
#
# Usage:
#   bash customizer.sh <package_id> <project_name> [app_display_name] [ios_team_id] [--keep-demo]
#   bash customizer.sh --clean [--apply]            # standalone demo removal (no identity change)
#
# Example:
#   bash customizer.sh com.mybank.app MyBankApp "My Bank" ABCDE12345
#   bash customizer.sh com.mybank.app MyBankApp "My Bank" ABCDE12345 --keep-demo
#
# What this does (identity mode):
#   1. Writes fork identity into gradle/libs.versions.toml (the single source of truth)
#   2. Runs ./gradlew syncForkConfig to propagate to iOS Config.xcconfig,
#      local.properties (Fastlane), and gradle.properties (rootProject.name)
#   3. Removes the demo showcase (scripts/remove-demo.sh) so the fork starts from a
#      clean, branded framework shell — this is the DEFAULT. Pass --keep-demo to retain
#      the Money-Toolkit demo (for exploring the framework's reference features).
#
# No source file scanning, no package renaming, no sync-dirs conflicts. The convention
# plugin derives all module namespaces from baseNamespace automatically. baseNamespace and
# fork identity are never touched by the demo removal.
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
  echo -e "${BOLD}Usage:${NC} bash customizer.sh <package_id> <project_name> [app_display_name] [ios_team_id]"
  echo
  echo -e "${BOLD}Examples:${NC}"
  echo "  bash customizer.sh com.mybank.app MyBankApp"
  echo "  bash customizer.sh com.mybank.app MyBankApp \"My Bank\" ABCDE12345"
  exit 2
fi

PACKAGE=$1
PROJECT_NAME=$2
APPNAME=${3:-$PROJECT_NAME}
TEAM_ID=${4:-"XXXXXXXXXX"}

# Derive baseNamespace: com.mybank.app → com.mybank
BASE_NS=$(echo "$PACKAGE" | rev | cut -d. -f2- | rev)

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
print_info "Base namespace:   $BASE_NS"
print_info "Project name:     $PROJECT_NAME"
print_info "Display name:     $APPNAME"
print_info "iOS Team ID:      $TEAM_ID"
echo

# ── Update libs.versions.toml ────────────────────────────────────────────────
print_info "Updating $LIBS_TOML..."

sed -i.bak "s|appId[[:space:]]*=.*|appId            = \"$PACKAGE\"|"             "$LIBS_TOML"
sed -i.bak "s|appDisplayName[[:space:]]*=.*|appDisplayName   = \"$APPNAME\"|"    "$LIBS_TOML"
sed -i.bak "s|baseNamespace[[:space:]]*=.*|baseNamespace    = \"$BASE_NS\"|"     "$LIBS_TOML"
sed -i.bak "s|projectName[[:space:]]*=.*|projectName      = \"$PROJECT_NAME\"|"  "$LIBS_TOML"
sed -i.bak "s|iosTeamId[[:space:]]*=.*|iosTeamId        = \"$TEAM_ID\"|"         "$LIBS_TOML"
find . -name "*.bak" -not -path "*/build/*" -delete

print_success "libs.versions.toml updated"

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
echo -e "${GREEN}${BOLD}✨ Customization complete!${NC}"
echo
echo -e "${YELLOW}Next steps:${NC}"
echo "  1. Replace cmp-android/google-services.json with your Firebase config"
echo "  2. Update fastlane-config/project_config.rb Firebase App IDs"
echo "  3. Replace cmp-ios/iosApp/Assets.xcassets with your app icon"
echo "  4. Run ./gradlew build to verify everything compiles"
echo
echo -e "  To update identity later: edit ${BOLD}gradle/libs.versions.toml${NC} → run ${BOLD}./gradlew syncForkConfig${NC}"
