#!/bin/bash

# sync-dirs.sh
# Script to sync directories and files from upstream repository

# Colors and formatting
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m'    # No Color
CHECKMARK='\xE2\x9C\x94'
CROSS='\xE2\x9C\x98'

# Default upstream URL
DEFAULT_UPSTREAM_URL="https://github.com/openMF/kmp-project-template.git"

# Script options
DRY_RUN=false
FORCE=false
ONLY_ITEMS=""   # --only <a,b,c>: restrict the sync to this subset of SYNC_DIRS/SYNC_FILES
LOG_FILE="sync-$(date +%d%m%Y-%H%M%S).log"

# Directories and files to sync
SYNC_DIRS=(
    "cmp-android"
    "cmp-desktop"
    "cmp-ios"
    "cmp-web"
    "cmp-shared"
    "cmp-navigation" # shared root-nav module (include(":cmp-navigation")) — was missing pre-2026-07
    "sync"           # shared :sync module (include(":sync")) — was missing pre-2026-07
    "core-base"
    "core"          # framework infra (incl. core/*/infra) syncs; **/demo/** + core/store seam preserved per-fork by sync_directory's convention block (base-branch re-assert)
    "build-logic"
    "deployment"     # 18-target deploy infra — consumer store metadata/screenshots + manifest/log excluded below
    "fastlane"
    "fastlane-config"
    "spotless"       # shared copyright/format config
    "gradle/wrapper" # Gradle wrapper (properties + jar) — pins the version across forks; NOT gradle/ root (libs.versions.toml is consumer-local + auto-healed)
    "maestro/screen-state" # framework E2E — core-base/ui screen-state retention flows (list-back, paging-restore, scroll-appkill, tab-switch); consumer feature flows live in other maestro/ subdirs + are preserved
    "scripts"
    "config"
    "secrets"        # secrets SCAFFOLD only — secrets/sample/** placeholder tree + LAYOUT.yaml sync; real secrets/live/** is gitignored + fork-local + excluded below (never synced)
    "docs"           # blueprint documentation — architecture patterns, claude pattern guides, deploy/ios/secrets/setup references; fork-ADDED docs survive (checkout never deletes fork-only files), template docs refresh
    ".github"
    ".run"
)

SYNC_FILES=(
    "Gemfile"
    "Gemfile.lock"
    "ci-prepush.bat"
    "ci-prepush.sh"
    "gradlew"                       # wrapper launcher scripts — pin the Gradle version with gradle/wrapper
    "gradlew.bat"
    "compose_compiler_config.conf"  # compose compiler config referenced by AndroidCompose.kt
    "sync-dirs.sh"                  # self-propagate: each sync updates the consumer's own copy for next time
    # --- blueprint infra files (2026-07 audit: full white-label blueprint coverage) ---
    ".editorconfig"                 # shared formatting rules
    ".gitattributes"                # shared line-ending / linguist rules
    ".gitignore"                    # shared ignore baseline (build dirs, sync-*.log, secrets, local.properties)
    ".actrc"                        # act (local GitHub Actions) config — pairs with .github/
    ".ruby-version"                 # Ruby pin for Fastlane — pairs with Gemfile/Gemfile.lock
    ".kover-floor.yml"              # coverage-floor config — pairs with config/ (kover)
    ".claudeignore"                 # Claude tooling ignore baseline
    # --- blueprint setup / customization scripts (root-level, not under scripts/) ---
    "setup-project.sh"              # master fork setup script
    "customizer.sh"                 # fork customization driver
    "keystore-manager.sh"           # keystore generate/encode/add operations
    "firebase-setup.sh"             # Firebase project configuration
    "generateModuleGraphs.sh"       # module dependency-graph generator
    # --- fork identity SCHEMA (the .template is committed + syncable; the filled-in
    #     gradle/fork.properties is gitignored + fork-local — NEVER synced) ---
    "gradle/fork.properties.template"
    "gradle/gradle-daemon-jvm.properties"  # pins the Gradle daemon JVM toolchain (17) + foojay URLs across forks
)

# Define exclusions for directories and files
# Format: "path/to/exclude:type"
# type can be 'dir' or 'file'
# Use "root" key for files in the root directory
declare -A EXCLUSIONS=(
    # Android — consumer-branded resources (drawables, strings, mipmaps), Firebase config,
    # launcher icon, and the dependency-guard baseline directory are preserved across syncs.
    ["cmp-android"]="src/main/res:dir dependencies:dir src/main/ic_launcher-playstore.png:file google-services.json:file"
    # iOS — consumer-branded asset catalog (app icon, color palette) preserved across syncs.
    ["cmp-ios"]="iosApp/Assets.xcassets:dir Configuration/Config.xcconfig:file"
    ["cmp-web"]="src/jsMain/resources:dir src/wasmJsMain/resources:dir"
    ["cmp-desktop"]="icons:dir build.gradle.kts:file"
    ["fastlane-config"]="project_config.rb:file extract_config.rb:file"
    # Deployment — sync the lane logic (_shared, Fastfile, per-target lane.rb, scripts) but
    # PRESERVE each consumer's store listings (metadata + screenshots), their per-project
    # deployment manifest, and their append-only promotion log. Secrets under deployment are
    # gitignored (never synced). Add new consumer-owned deployment paths here if they appear.
    ["deployment"]="android/metadata:dir android/screenshots:dir ios/appstore/metadata:dir ios/screenshots:dir desktop/mac-app-store/metadata:dir desktop/mac-app-store/screenshots:dir fastlane/metadata:dir DEPLOYMENT_MANIFEST.yaml:file PROMOTION_LOG.yaml:file"
    [".github"]="workflows/sync-dirs.yaml:file"
    # Secrets — sync the STRUCTURE (secrets/sample/** placeholder scaffold + LAYOUT.yaml)
    # so forks inherit the canonical layout, but NEVER the real values: secrets/live/ is
    # gitignored + fork-local (preserved-then-restored here as defense-in-depth even though
    # git checkout never touches gitignored paths), and .sync-meta.json is per-fork vault
    # sync state (SV33) — both excluded.
    ["secrets"]="live:dir .sync-meta.json:file"
    # ["root"]="secrets.env:file"  — REMOVED: secrets.env is retired (2026-06-23).
    # Keystore DN now lives in gradle/fork.properties — that file is GITIGNORED and
    # fork-local (each fork's filled-in identity), so it is NEVER synced. Only the
    # committed schema gradle/fork.properties.template syncs (see SYNC_FILES above).
    # Keystore passwords live in
    # secrets/live/android/keystores/ per-value files (gitignored, not synced).
    # DO NOT REMOVE — preserves consumer-specific flavor extensions across syncs.
    # Each downstream consumer app (mifos-mobile, mifos-pay, mifos-x-field-officer-app,
    # mifos-x-group-banking, mifos-x-open-banking, reels-downloader-new, ...) may
    # create build-logic/convention/src/main/kotlin/local/LocalFlavors.kt to add
    # their own flavors / dimensions / overrides on top of the synced base.
    # See docs/architecture/FLAVORS_EXTENSION.md for the pattern.
    ["build-logic"]="convention/src/main/kotlin/local:dir"
)

# Display help information
show_help() {
    echo -e "${BOLD}Usage:${NC} ./sync-dirs.sh [options]"
    echo
    echo -e "${BOLD}Description:${NC}"
    echo "  This script syncs directories and files from an upstream repository."
    echo "  It preserves excluded files and directories as defined in the script."
    echo
    echo -e "${BOLD}Options:${NC}"
    echo "  -h, --help      Display this help message and exit"
    echo "  --dry-run       Show what would be done without making changes"
    echo "  -f, --force     Skip confirmation prompts and proceed automatically"
    echo "  --only <list>   Restrict the sync to a comma/space-separated subset of the declared"
    echo "                  SYNC_DIRS/SYNC_FILES (e.g. --only build-logic,gradle/wrapper,spotless)."
    echo "                  Use it when a diverged fork only wants specific infra refreshed."
    echo
    echo -e "${BOLD}Examples:${NC}"
    echo "  ./sync-dirs.sh              # Run with interactive prompts"
    echo "  ./sync-dirs.sh --dry-run    # Test run without changes"
    echo "  ./sync-dirs.sh --force      # Run with no prompts"
    echo "  ./sync-dirs.sh --only build-logic,gradle/wrapper --dry-run   # only that infra subset"
}

# Logging function
log_message() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" >> "$LOG_FILE"
    echo -e "$1"
}

# Error handling function
handle_error() {
    log_message "${RED}${CROSS} Error: $1${NC}"
    exit 1
}

# Print error message
print_error() {
    log_message "${RED}${CROSS} Error: $1${NC}"
}

# Simple progress indicator function
show_progress() {
    if [ "$DRY_RUN" = false ]; then
        echo -ne "${BLUE}[                    ]${NC}\r"
        echo -ne "${BLUE}[=====               ]${NC}\r"
        sleep 0.1
        echo -ne "${BLUE}[==========          ]${NC}\r"
        sleep 0.1
        echo -ne "${BLUE}[===============     ]${NC}\r"
        sleep 0.1
        echo -ne "${BLUE}[====================]${NC}"
        echo
    fi
}

# Fancy banner
print_banner() {
    echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${BOLD}        Project Directory Sync Tool         ${NC}${BLUE}║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
    echo
}

# Print step with color and symbol
print_step() {
    log_message "${GREEN}${CHECKMARK} $1${NC}"
}

# Print warning with color
print_warning() {
    log_message "${YELLOW}⚠ $1${NC}"
}

# Function to generate unique branch name
get_sync_branch_name() {
    local date_suffix=$(date +%Y%m%d-%H%M%S)
    echo "sync/upstream-${date_suffix}"
}

# Print directories and files to be synced
print_items() {
    echo -e "${BLUE}Items to sync:${NC}"
    echo -e "${BOLD}Directories:${NC}"
    for dir in "${SYNC_DIRS[@]}"; do
        echo -e "  ${BOLD}→${NC} $dir"
    done

    echo -e "\n${BOLD}Files:${NC}"
    for file in "${SYNC_FILES[@]}"; do
        echo -e "  ${BOLD}→${NC} $file"
    done
    echo
}

# Function to check if a path is excluded
is_excluded() {
    local check_dir=$1
    local full_path=$2
    local check_type=$3  # 'file' or 'dir'

    # Remove ./ from the beginning of the path if it exists
    full_path="${full_path#./}"

    # ── Convention exclusions (showcase-framework-separation) ───────────────
    # core/ is synced framework-only. Never overwrite a fork's removed demo
    # (**/demo/**) or the per-fork core/store seam files (D8). These are matched
    # by convention (pattern), not the exact-path EXCLUSIONS map above.
    case "$full_path" in
        */demo/*)                       return 0 ;;  # demo showcase packages
        */demo)                         return 0 ;;  # a demo/ dir itself
        core/store/*AppScreenStateDefaults.kt) return 0 ;;  # fork seam (D8)
        core/store/*AppErrorMapper.kt)         return 0 ;;
        core/store/*AppStoreRegistry.kt)       return 0 ;;
        core/store/*StoreModule.kt)            return 0 ;;
    esac

    # Check for root-level exclusions
    if [ -n "${EXCLUSIONS["root"]}" ] && [[ "$check_type" == "file" ]]; then
        local IFS=' '
        read -ra ROOT_EXCLUDE_ITEMS <<< "${EXCLUSIONS["root"]}"

        for item in "${ROOT_EXCLUDE_ITEMS[@]}"; do
            local IFS=':'
            read -ra PARTS <<< "$item"
            local exclude_path="${PARTS[0]}"
            local exclude_type="${PARTS[1]}"

            if [ "$exclude_type" = "$check_type" ] && [ "$full_path" = "$exclude_path" ]; then
                return 0  # Path is excluded
            fi
        done
    fi

    # Check directory-specific exclusions
    for dir in "${!EXCLUSIONS[@]}"; do
        # Skip the root key as we've already checked it
        if [ "$dir" = "root" ]; then
            continue
        fi

        # Check if the path starts with the directory we're looking at
        if [[ "$full_path" == "$dir"* ]]; then
            local IFS=' '
            read -ra EXCLUDE_ITEMS <<< "${EXCLUSIONS[$dir]}"

            for item in "${EXCLUDE_ITEMS[@]}"; do
                local IFS=':'
                read -ra PARTS <<< "$item"
                local exclude_path="$dir/${PARTS[0]}"
                local exclude_type="${PARTS[1]}"

                # Remove any duplicate slashes
                exclude_path=$(echo "$exclude_path" | sed 's#/\+#/#g')
                full_path=$(echo "$full_path" | sed 's#/\+#/#g')

                if [ "$exclude_type" = "$check_type" ] && [ "$full_path" = "$exclude_path" ]; then
                    return 0  # Path is excluded
                fi
            done
        fi
    done
    return 1  # Path is not excluded
}

cleanup_temp_dirs() {
    print_step "Cleaning up temporary directories..."
    find . -type d -name "temp_*" -exec rm -rf {} +
    show_progress
}

# Function to preserve excluded paths
preserve_excluded_paths() {
    local dir=$1
    local destination=$2

    if [ -n "${EXCLUSIONS[$dir]}" ]; then
        local IFS=' '
        read -ra EXCLUDE_ITEMS <<< "${EXCLUSIONS[$dir]}"

        for item in "${EXCLUDE_ITEMS[@]}"; do
            local IFS=':'
            read -ra PARTS <<< "$item"
            local exclude_path="${PARTS[0]}"
            local exclude_type="${PARTS[1]}"
            local full_source_path="$dir/$exclude_path"
            local full_dest_path="$destination/$exclude_path"

            if [ -e "$full_source_path" ]; then
                print_step "Preserving excluded ${exclude_type}: ${BOLD}$exclude_path${NC}"
                mkdir -p "$(dirname "$full_dest_path")"
                cp -r "$full_source_path" "$(dirname "$full_dest_path")"
            fi
        done
    fi
}

# Function to sync directory with exclusions
sync_directory() {
    local dir=$1
    local temp_branch=$2

    if [ -d "$dir" ]; then
        print_step "Syncing ${BOLD}$dir${NC}..."

        if [ "$DRY_RUN" = false ]; then
            # Create temporary directory for original content
            mkdir -p "temp_$dir"

            # Store original directory for excluded items
            if [ -d "$dir" ]; then
                # First handle directory exclusions
                if [ -n "${EXCLUSIONS[$dir]}" ]; then
                    local IFS=' '
                    read -ra EXCLUDE_ITEMS <<< "${EXCLUSIONS[$dir]}"

                    for item in "${EXCLUDE_ITEMS[@]}"; do
                        local IFS=':'
                        read -ra PARTS <<< "$item"
                        local exclude_path="$dir/${PARTS[0]}"
                        local exclude_type="${PARTS[1]}"

                        if [ "$exclude_type" = "dir" ] && [ -e "$exclude_path" ]; then
                            print_step "Preserving excluded directory: ${BOLD}${PARTS[0]}${NC}"
                            mkdir -p "$(dirname "temp_$exclude_path")"
                            cp -r "$exclude_path" "$(dirname "temp_$exclude_path")"
                        elif [ "$exclude_type" = "file" ] && [ -f "$exclude_path" ]; then
                            print_step "Preserving excluded file: ${BOLD}${PARTS[0]}${NC}"
                            mkdir -p "$(dirname "temp_$exclude_path")"
                            cp "$exclude_path" "temp_$exclude_path"
                        fi
                    done
                fi
            fi

            # Checkout from upstream
            git checkout "$temp_branch" -- "$dir" || {
                print_error "Failed to sync $dir"
                rm -rf "temp_$dir"
                return 1
            }

            # ── Convention exclusions on a DIRECTORY sync (the is_excluded rules:
            #    **/demo/** + the core/store seam). Without this, a whole-dir sync of
            #    `core`/`core-base` clobbers the fork's branded seam files and re-adds
            #    demo packages the fork removed via `customizer --clean`. Re-assert the
            #    fork's own state from the base branch (BASE_BRANCH) for every synced
            #    path the convention flags: restore the fork's version if it had one,
            #    else drop the upstream-added file the fork never had.
            while IFS= read -r conv_f; do
                [ -z "$conv_f" ] && continue
                if is_excluded "$dir" "$conv_f" "file"; then
                    if git cat-file -e "${BASE_BRANCH}:${conv_f}" 2>/dev/null; then
                        git checkout "$BASE_BRANCH" -- "$conv_f" 2>/dev/null \
                            && print_step "Preserved fork's ${BOLD}$conv_f${NC} (convention)"
                    else
                        git rm -f --quiet "$conv_f" 2>/dev/null || rm -f "$conv_f"
                        print_step "Dropped upstream-added ${BOLD}$conv_f${NC} (fork removed it)"
                    fi
                fi
            done < <(git diff --name-only "$BASE_BRANCH" -- "$dir" 2>/dev/null)

            # ── 3-way merge for merge-owned files (customization-surface contract) ──
            # Replaces a blind upstream clobber with a real merge so fork edits (e.g.
            # AndroidManifest permissions) survive a template update. ours=fork
            # (BASE_BRANCH), theirs=upstream (temp_branch), base=merge-base. Guarded:
            # no-op on forks that don't ship the reader.
            if declare -F cs_match_g >/dev/null 2>&1 && declare -F cs_merge >/dev/null 2>&1; then
                local _mbase; _mbase="$(git merge-base "$BASE_BRANCH" "$temp_branch" 2>/dev/null)"
                while IFS= read -r _mf; do
                    [ -z "$_mf" ] && continue
                    cs_match_g "$_mf"; [ "${CS_M_OWNER:-}" = "merge" ] || continue
                    local _o _b _t; _o="$(mktemp)"; _b="$(mktemp)"; _t="$(mktemp)"
                    if git show "$BASE_BRANCH:$_mf" > "$_o" 2>/dev/null \
                       && git show "$temp_branch:$_mf" > "$_t" 2>/dev/null; then
                        git show "${_mbase}:$_mf" > "$_b" 2>/dev/null || cp "$_o" "$_b"
                        mkdir -p "$(dirname "$_mf")"
                        if cs_merge "${CS_M_STRAT:-3way}" "$_o" "$_b" "$_t" "$_mf"; then
                            print_step "Merged (${CS_M_STRAT:-3way}) ${BOLD}$_mf${NC} — fork edits preserved"
                        else
                            print_warning "CONFLICT in ${BOLD}$_mf${NC} (${CS_M_STRAT:-3way}) — resolve markers before committing the sync"
                        fi
                    fi
                    rm -f "$_o" "$_b" "$_t"
                done < <(git diff --name-only "$BASE_BRANCH" "$temp_branch" -- "$dir" 2>/dev/null)
            fi

            # Restore excluded files and directories
            if [ -n "${EXCLUSIONS[$dir]}" ]; then
                local IFS=' '
                read -ra EXCLUDE_ITEMS <<< "${EXCLUSIONS[$dir]}"

                for item in "${EXCLUDE_ITEMS[@]}"; do
                    local IFS=':'
                    read -ra PARTS <<< "$item"
                    local exclude_path="$dir/${PARTS[0]}"
                    local exclude_type="${PARTS[1]}"
                    local temp_path="temp_$exclude_path"

                    if [ -e "$temp_path" ]; then
                        print_step "Restoring excluded ${exclude_type}: ${BOLD}${PARTS[0]}${NC}"
                        mkdir -p "$(dirname "$exclude_path")"
                        if [ "$exclude_type" = "dir" ]; then
                            rm -rf "$exclude_path"
                            cp -r "$temp_path" "$(dirname "$exclude_path")"
                        else
                            cp "$temp_path" "$exclude_path"
                        fi
                    fi
                done
            fi
        fi
    else
        print_warning "Directory ${BOLD}$dir${NC} not found. Creating it..."
        if [ "$DRY_RUN" = false ]; then
            mkdir -p "$dir"
            git checkout "$temp_branch" -- "$dir" || {
                handle_error "Failed to sync $dir"
                cleanup_temp_dirs
            }
        fi
    fi
    show_progress
}

# Function to sync individual file with exclusions
sync_file() {
    local file=$1
    local temp_branch=$2

    # Check if file should be excluded (root-level or directory-specific)
    if is_excluded "$(dirname "$file")" "$file" "file"; then
        print_step "Skipping excluded file: ${BOLD}$file${NC}"
        return
    fi

    print_step "Syncing ${BOLD}$file${NC}..."
    if [ "$DRY_RUN" = false ]; then
        if [ -f "$file" ]; then
            # Create directory for excluded files if it doesn't exist
            mkdir -p "temp_files"
            # Store original file if it exists
            cp "$file" "temp_files/$(basename "$file")"
        fi

        if ! git checkout "$temp_branch" -- "$file"; then
            if [ -f "temp_files/$(basename "$file")" ]; then
                # Restore original file if checkout fails
                cp "temp_files/$(basename "$file")" "$file"
            fi
            print_error "Failed to sync $file"
            return 1
        fi
    fi
    show_progress
}

# Function to get default branch name
get_default_branch() {
    local default_branch
    default_branch=$(git remote show origin | grep 'HEAD branch' | cut -d' ' -f5)
    echo "$default_branch"
}

# Function to preserve root-level excluded files
preserve_root_files() {
    if [ -n "${EXCLUSIONS["root"]}" ] && [ "$DRY_RUN" = false ]; then
        print_step "Preserving root-level excluded files..."
        mkdir -p "temp_root"

        local IFS=' '
        read -ra ROOT_EXCLUDE_ITEMS <<< "${EXCLUSIONS["root"]}"

        for item in "${ROOT_EXCLUDE_ITEMS[@]}"; do
            local IFS=':'
            read -ra PARTS <<< "$item"
            local exclude_path="${PARTS[0]}"
            local exclude_type="${PARTS[1]}"

            if [ "$exclude_type" = "file" ] && [ -f "$exclude_path" ]; then
                print_step "Preserving root file: ${BOLD}$exclude_path${NC}"
                cp "$exclude_path" "temp_root/"
            fi
        done
    fi
}

# Function to restore root-level excluded files
restore_root_files() {
    if [ -n "${EXCLUSIONS["root"]}" ] && [ "$DRY_RUN" = false ]; then
        print_step "Restoring root-level excluded files..."

        local IFS=' '
        read -ra ROOT_EXCLUDE_ITEMS <<< "${EXCLUSIONS["root"]}"

        for item in "${ROOT_EXCLUDE_ITEMS[@]}"; do
            local IFS=':'
            read -ra PARTS <<< "$item"
            local exclude_path="${PARTS[0]}"
            local exclude_type="${PARTS[1]}"

            if [ "$exclude_type" = "file" ] && [ -f "temp_root/$(basename "$exclude_path")" ]; then
                print_step "Restoring root file: ${BOLD}$exclude_path${NC}"
                cp "temp_root/$(basename "$exclude_path")" "$exclude_path"
            fi
        done

        rm -rf "temp_root"
    fi
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        -f|--force)
            FORCE=true
            shift
            ;;
        --only)
            ONLY_ITEMS="${2:-}"
            [ -z "$ONLY_ITEMS" ] && handle_error "--only requires a comma/space-separated list of dirs/files (e.g. --only build-logic,gradle/wrapper)"
            shift 2
            ;;
        *)
            handle_error "Unknown option: $1. Use --help for usage information."
            ;;
    esac
done

# --only: restrict SYNC_DIRS/SYNC_FILES to the named subset (targeted sync for diverged forks
# that only want specific infra refreshed). Each named item MUST already be a declared SYNC_DIR
# or SYNC_FILE — this narrows the canonical set, it never adds un-vetted paths.
if [ -n "$ONLY_ITEMS" ]; then
    ONLY_ITEMS="${ONLY_ITEMS//,/ }"
    declare -a _KEEP_DIRS=() _KEEP_FILES=()
    for _want in $ONLY_ITEMS; do
        _found=false
        for _d in "${SYNC_DIRS[@]}";  do [ "$_d" = "$_want" ] && { _KEEP_DIRS+=("$_want");  _found=true; break; }; done
        for _f in "${SYNC_FILES[@]}"; do [ "$_f" = "$_want" ] && { _KEEP_FILES+=("$_want"); _found=true; break; }; done
        [ "$_found" = false ] && handle_error "--only: '$_want' is not a declared SYNC_DIR or SYNC_FILE. Choose from: ${SYNC_DIRS[*]} ${SYNC_FILES[*]}"
    done
    SYNC_DIRS=("${_KEEP_DIRS[@]}")
    SYNC_FILES=("${_KEEP_FILES[@]}")
    echo -e "${YELLOW}${BOLD}Targeted sync (--only):${NC} ${SYNC_DIRS[*]} ${SYNC_FILES[*]}"
fi

# Check git configuration
if ! git config user.name > /dev/null || ! git config user.email > /dev/null; then
    handle_error "Git user.name or user.email not configured"
fi

# Main script
clear
print_banner
print_items

# Print configured exclusions
echo -e "${BLUE}Configured Exclusions:${NC}"
OLD_IFS="$IFS"  # Save original IFS
for dir in "${!EXCLUSIONS[@]}"; do
    echo -e "  ${BOLD}${dir}${NC}:"
    IFS=' '
    read -ra EXCLUDE_ITEMS <<< "${EXCLUSIONS[$dir]}"
    for item in "${EXCLUDE_ITEMS[@]}"; do
        IFS=':'
        read -ra PARTS <<< "$item"
        if [ "$dir" = "root" ]; then
            echo -e "    → ${PARTS[0]} (${PARTS[1]}) [root level]"
        else
            echo -e "    → ${PARTS[0]} (${PARTS[1]})"
        fi
    done
done
IFS="$OLD_IFS"  # Restore original IFS
echo

# Check if upstream remote exists
if ! git remote | grep -q '^upstream$'; then
    print_warning "Upstream remote not found."
    if [ "$DRY_RUN" = false ]; then
        echo -e "${YELLOW}Default upstream URL:${NC} ${BOLD}$DEFAULT_UPSTREAM_URL${NC}"
        if [ "$FORCE" = false ]; then
            echo -e "${YELLOW}Press Enter to use default URL or input a different one:${NC}"
            read -r custom_url
        else
            custom_url=""
        fi

        upstream_url=${custom_url:-$DEFAULT_UPSTREAM_URL}
        print_step "Adding upstream remote: ${BOLD}$upstream_url${NC}"
        git remote add upstream "$upstream_url" || handle_error "Failed to add upstream remote"
        show_progress
    fi
fi

# Fetch from upstream
print_step "Fetching from upstream..."
if ! git fetch upstream; then
    handle_error "Failed to fetch from upstream"
fi
show_progress

# Get default branch if dev doesn't exist
DEFAULT_BRANCH=$(get_default_branch)
BASE_BRANCH="dev"
if ! git rev-parse --verify "origin/dev" >/dev/null 2>&1; then
    print_warning "dev branch not found, using default branch: ${BOLD}$DEFAULT_BRANCH${NC}"
    BASE_BRANCH="$DEFAULT_BRANCH"
fi

# Create sync branch
SYNC_BRANCH=$(get_sync_branch_name)
print_step "Creating sync branch: ${BOLD}$SYNC_BRANCH${NC}"

if [ "$DRY_RUN" = false ]; then
    # Create sync branch from base branch
    if ! git checkout -b "$SYNC_BRANCH" "$BASE_BRANCH"; then
        handle_error "Failed to create sync branch"
    fi
    show_progress

    # Create temporary branch for upstream changes
    TEMP_BRANCH="temp-${SYNC_BRANCH}"
    print_step "Creating temporary branch: ${BOLD}$TEMP_BRANCH${NC}"
    if ! git checkout -b "$TEMP_BRANCH" "upstream/$BASE_BRANCH"; then
        handle_error "Failed to create temporary branch"
    fi
    show_progress

    # Switch back to sync branch
    print_step "Switching back to sync branch..."
    if ! git checkout "$SYNC_BRANCH"; then
        handle_error "Failed to switch to sync branch"
    fi
    show_progress

    # Preserve root-level excluded files
    preserve_root_files
fi

# ── customization-surface advisory (fork-ownership contract) ──────────────────
# Non-breaking: reports which about-to-be-synced paths are `merge`-owned per
# customization-surface.yaml, so a maintainer can confirm the EXCLUSIONS map
# preserves fork edits (e.g. AndroidManifest permissions). The mechanical sync
# below is UNCHANGED — the contract is the declared source of truth the merge
# engine adopts next. Fully guarded so it can never abort a sync.
CS_READER="$(dirname "$0")/scripts/customization-surface.sh"
if [ -f "$CS_READER" ] && [ -f "$(dirname "$0")/customization-surface.yaml" ]; then
    # shellcheck source=/dev/null
    source "$CS_READER" 2>/dev/null || true
    if declare -F cs_match_g >/dev/null 2>&1; then
        echo -e "\n${BLUE}${BOLD}Fork-ownership contract — merge-owned paths in the sync surface${NC}"
        cs_merge_hits=0
        while IFS= read -r _csf; do
            cs_match_g "$_csf" || continue
            if [ "${CS_M_OWNER:-}" = "merge" ]; then
                echo -e "  ${YELLOW}merge${NC} $_csf ${YELLOW}(${CS_M_STRAT:-3way} — do NOT blind-overwrite)${NC}"
                cs_merge_hits=$((cs_merge_hits + 1))
            fi
        done < <(git ls-files -- "${SYNC_DIRS[@]}" 2>/dev/null)
        [ "$cs_merge_hits" -gt 0 ] && echo -e "  ${YELLOW}⚠ $cs_merge_hits merge-owned path(s) — verify EXCLUSIONS preserves fork edits (permissions, catalog, nav).${NC}"
    fi
fi

# Sync directories
echo -e "\n${BLUE}${BOLD}Syncing directories...${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
for dir in "${SYNC_DIRS[@]}"; do
    sync_directory "$dir" "$TEMP_BRANCH"
done

# Sync files
echo -e "\n${BLUE}${BOLD}Syncing files...${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"
for file in "${SYNC_FILES[@]}"; do
    sync_file "$file" "$TEMP_BRANCH"
done

# Self-heal libs.versions.toml — pull missing build-logic aliases from upstream.
# `build-logic/` is sync'd but `gradle/libs.versions.toml` is consumer-local (it
# carries project-specific package names + version overrides). When upstream adds
# new `libs.<X>` accessors in build-logic with their matching catalog entries,
# only the build-logic gets sync'd — the consumer's catalog falls behind and the
# synced build-logic references aliases that don't exist locally. This function
# detects that gap and pulls the missing alias entries (and their referenced
# version keys) directly from the upstream catalog (`$TEMP_BRANCH`).
heal_libs_versions_toml() {
    local libs_toml="gradle/libs.versions.toml"

    if [ ! -f "$libs_toml" ]; then
        return 0
    fi
    if [ ! -d "build-logic" ]; then
        return 0
    fi

    echo -e "\n${BLUE}${BOLD}Healing libs.versions.toml...${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"

    local raw_refs
    raw_refs=$(grep -rhoE '\blibs\.[a-zA-Z][a-zA-Z0-9._]*' \
            --include='*.kt' --include='*.kts' \
            build-logic 2>/dev/null \
        | sed -E 's/^libs\.//' \
        | sort -u)

    # Method-call accessors are not alias references — they take string args.
    local method_re='^(findLibrary|findVersion|findBundle|findPlugin)([(.]|$)'

    # Cache upstream's catalog content once.
    local upstream_toml
    upstream_toml=$(git show "${TEMP_BRANCH}:${libs_toml}" 2>/dev/null || true)
    if [ -z "$upstream_toml" ]; then
        print_warning "Upstream has no ${libs_toml} — skipping heal."
        return 0
    fi

    # Helper: insert a line right after [section] in the consumer's catalog.
    insert_into_section() {
        local section_name="$1"
        local line="$2"
        local line_no
        line_no=$(grep -n "^\[${section_name}\]" "$libs_toml" | head -1 | cut -d: -f1)
        if [ -z "$line_no" ]; then
            print_warning "No [${section_name}] section header — skipping insert of '$line'"
            return 1
        fi
        { head -n "$line_no" "$libs_toml"; echo "$line"; tail -n +$((line_no + 1)) "$libs_toml"; } > "${libs_toml}.tmp"
        mv "${libs_toml}.tmp" "$libs_toml"
    }

    local healed=0
    local warnings=0
    while IFS= read -r ref; do
        [ -z "$ref" ] && continue
        [[ "$ref" =~ $method_re ]] && continue

        local section="libraries"
        local lookup="$ref"
        case "$ref" in
            plugins.*)
                section="plugins"
                lookup="${ref#plugins.}"
                ;;
            versions.*)
                section="versions"
                lookup="${ref#versions.}"
                [ "$lookup" = "toml" ] && continue
                ;;
        esac
        local key
        key=$(echo "$lookup" | sed 's/\./-/g')

        if grep -qE "^${key}[[:space:]]*=" "$libs_toml"; then
            continue
        fi

        local upstream_line
        upstream_line=$(echo "$upstream_toml" | grep -E "^${key}[[:space:]]*=" | head -1)
        if [ -z "$upstream_line" ]; then
            print_warning "Missing alias '$key' (for libs.$ref) not present in upstream catalog either — manual fix needed."
            warnings=$((warnings + 1))
            continue
        fi

        # Pull the version key too, if the alias version.refs something missing.
        local ref_version
        ref_version=$(echo "$upstream_line" | grep -oE 'version\.ref = "[^"]+"' | sed -E 's/version\.ref = "([^"]+)"/\1/' | head -1)
        if [ -n "$ref_version" ] && ! grep -qE "^${ref_version}[[:space:]]*=" "$libs_toml"; then
            local ver_line
            ver_line=$(echo "$upstream_toml" | grep -E "^${ref_version}[[:space:]]*=" | head -1)
            if [ -n "$ver_line" ]; then
                if insert_into_section "versions" "$ver_line"; then
                    echo -e "  ${GREEN}${CHECKMARK}${NC} Added version '${ref_version}' (referenced by '${key}') to [versions]"
                    healed=$((healed + 1))
                fi
            fi
        fi

        if insert_into_section "$section" "$upstream_line"; then
            echo -e "  ${GREEN}${CHECKMARK}${NC} Added '${key}' to [${section}] from upstream"
            healed=$((healed + 1))
        fi
    done <<< "$raw_refs"

    if [ $healed -gt 0 ]; then
        echo -e "\n${GREEN}${BOLD}🩹 Healed ${healed} entries in ${libs_toml} — they will be included in the sync.${NC}"
    elif [ $warnings -gt 0 ]; then
        print_warning "${warnings} missing aliases could not be auto-healed (not in upstream either). Manual fix needed."
    else
        echo -e "  ${GREEN}${CHECKMARK}${NC} libs.versions.toml already in sync with build-logic — no heal needed."
    fi
}

if [ "$DRY_RUN" = false ]; then
    # Restore root-level excluded files
    restore_root_files

    cleanup_temp_dirs
    rm -rf temp_files

    # Self-heal the version catalog BEFORE we drop the temp branch (we need it
    # to access upstream's libs.versions.toml).
    heal_libs_versions_toml

    # Cleanup temporary branch
    print_step "Cleaning up temporary branch..."
    git branch -D "$TEMP_BRANCH" || handle_error "Failed to delete temporary branch"
    show_progress

    # Check for changes
    if ! git diff --quiet --exit-code --cached; then
        print_step "Committing changes..."
        git add "${SYNC_DIRS[@]}" "${SYNC_FILES[@]}"
        # Include any catalog heal additions made by heal_libs_versions_toml.
        if [ -f "gradle/libs.versions.toml" ]; then
            git add "gradle/libs.versions.toml"
        fi
        git commit -m "sync: Update directories and files from upstream

This PR syncs the following items with upstream:
- Directories: ${SYNC_DIRS[*]}
- Files: ${SYNC_FILES[*]}
- Auto-heal: gradle/libs.versions.toml (pulls missing build-logic aliases from upstream)" || handle_error "Failed to commit changes"
        show_progress

        if [ "$FORCE" = false ]; then
            echo -e "\n${YELLOW}${BOLD}Would you like to push the sync branch? (y/n)${NC}"
            read -r response
            if [[ "$response" =~ ^[Yy]$ ]]; then
                print_step "Pushing sync branch..."
                git push -u origin "$SYNC_BRANCH" || handle_error "Failed to push sync branch"
                show_progress
                echo -e "\n${GREEN}${BOLD}✨ Sync branch pushed successfully! ✨${NC}"
                echo -e "${YELLOW}Please create a pull request from branch ${BOLD}$SYNC_BRANCH${NC}${YELLOW} to ${BOLD}$BASE_BRANCH${NC}${YELLOW} in your repository.${NC}\n"
            else
                echo -e "\n${YELLOW}Changes committed but not pushed. You can push later with:${NC}"
                echo -e "${BOLD}git push -u origin $SYNC_BRANCH${NC}"
                echo -e "${YELLOW}Then create a pull request from ${BOLD}$SYNC_BRANCH${NC}${YELLOW} to ${BOLD}$BASE_BRANCH${NC}\n"
            fi
        else
            print_step "Pushing sync branch..."
            git push -u origin "$SYNC_BRANCH" || handle_error "Failed to push sync branch"
            show_progress
            echo -e "\n${GREEN}${BOLD}✨ Sync branch pushed successfully! ✨${NC}"
            echo -e "${YELLOW}Please create a pull request from branch ${BOLD}$SYNC_BRANCH${NC}${YELLOW} to ${BOLD}$BASE_BRANCH${NC}${YELLOW} in your repository.${NC}\n"
        fi
    else
        print_warning "No changes to commit"
        git checkout "$BASE_BRANCH"
        git branch -D "$SYNC_BRANCH"
    fi
else
    echo -e "\n${YELLOW}${BOLD}Dry run completed. No changes were made.${NC}\n"
fi
