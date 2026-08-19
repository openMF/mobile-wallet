#!/usr/bin/env bash
# scripts/store/sync-play-listing.sh
#
# Upload Play Store store listing (title, description, screenshots, feature graphic)
# to Google Play Console without uploading a new APK/AAB.
#
# Prerequisites:
#   - secrets/live/android/play/playStorePublishServiceCredentialsFile.json  (Google Play service account key)
#   - deployment/android/metadata/       (Fastlane supply metadata tree)
#       en-US/title.txt, short_description.txt, full_description.txt
#       images/phoneScreenshots/*.png
#       images/featureGraphic.png
#
# Usage:
#   bash scripts/store/sync-play-listing.sh
#   bash scripts/store/sync-play-listing.sh --dry-run     # validate config only
#   bash scripts/store/sync-play-listing.sh --track beta  # target a specific track

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"

# ── Config ────────────────────────────────────────────────────────────────────

# Resolve via build-secrets (secrets/LAYOUT.yaml SoT) — no static path/name.
JSON_KEY="$REPO_ROOT/$("$REPO_ROOT/deployment/scripts/build-secrets" path play_service_account 2>/dev/null)"
METADATA_PATH="deployment/android/metadata"
PACKAGE_NAME="$(grep -E '^\s*appId\s*=' gradle/libs.versions.toml 2>/dev/null \
    | head -1 | sed 's/[^"]*"\([^"]*\)".*/\1/' || echo "")"
TRACK="${FASTLANE_TRACK:-internal}"
DRY_RUN=false

for arg in "$@"; do
    case "$arg" in
        --dry-run) DRY_RUN=true ;;
        --track)   shift; TRACK="$1" ;;
        --track=*) TRACK="${arg#*=}" ;;
    esac
done

# ── Preflight checks ──────────────────────────────────────────────────────────

if [[ ! -f "$JSON_KEY" ]]; then
    echo "[ERROR] Play Store service account not found at $JSON_KEY" >&2
    echo "        Run: /secrets pull  (vault mode) or place the file manually." >&2
    exit 1
fi

if [[ -z "$PACKAGE_NAME" ]]; then
    echo "[ERROR] Could not read appId from gradle/libs.versions.toml" >&2
    exit 1
fi

if [[ ! -f "$METADATA_PATH/en-US/title.txt" ]]; then
    echo "[ERROR] Metadata not found at $METADATA_PATH/en-US/title.txt" >&2
    echo "        Run: ./gradlew syncForkConfig  to regenerate from fork.properties" >&2
    exit 1
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Play Store Listing Sync"
echo "  Package:   $PACKAGE_NAME"
echo "  Track:     $TRACK"
echo "  Metadata:  $METADATA_PATH"
echo "  Title:     $(cat "$METADATA_PATH/en-US/title.txt" 2>/dev/null || echo '(missing)')"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

if [[ "$DRY_RUN" == "true" ]]; then
    echo "[DRY RUN] Would upload listing for $PACKAGE_NAME — exiting without upload."
    exit 0
fi

# ── Ruby/bundler setup ────────────────────────────────────────────────────────

# Prefer Homebrew Ruby 3 if system Ruby is too old
for ruby_candidate in \
        "/opt/homebrew/opt/ruby@3.3/bin/ruby" \
        "/opt/homebrew/opt/ruby@3/bin/ruby" \
        "$(which ruby 2>/dev/null)"; do
    if [[ -x "$ruby_candidate" ]] && \
       "$ruby_candidate" -e 'exit(RUBY_VERSION.split(".")[0].to_i >= 3 ? 0 : 1)' 2>/dev/null; then
        RUBY_BIN="$ruby_candidate"
        BUNDLE_BIN="$(dirname "$RUBY_BIN")/bundle"
        break
    fi
done

if [[ -z "${RUBY_BIN:-}" ]]; then
    echo "[ERROR] Ruby 3+ not found. Install via: brew install ruby@3.3" >&2
    exit 1
fi

export PATH="$(dirname "$RUBY_BIN"):$PATH"

echo "[INFO] Using Ruby: $("$RUBY_BIN" --version)"
echo ""

# ── Run via direct Google Play API (bypasses Fastlane supply track requirement) ─
# Fastlane supply's upload_to_play_store requires at least one existing release in
# the track before it can update metadata ("Could not find release for version code ''").
# The direct API approach works on brand-new apps with no releases yet.

"$BUNDLE_BIN" exec ruby "$REPO_ROOT/scripts/store/sync-play-listing.rb" "$@"

echo ""
echo "✓ Play Store listing updated for $PACKAGE_NAME"
echo "  View in Play Console: https://play.google.com/console/developers"
echo ""
echo "  NOTE: If you see 'Package not found', the app must first be created"
echo "        in Play Console by uploading an APK/AAB via the UI or:"
echo "        bundle exec fastlane android deployInternal"
