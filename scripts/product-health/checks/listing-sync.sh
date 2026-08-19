#!/usr/bin/env bash
# checks/listing-sync.sh — the store listing must sync to the store on EVERY store deploy state,
# not only on a fresh build or a /idea-deploy-orchestrated run.
#
# The listing is DERIVED from app-profile/ (before_all → syncForkConfig → deployment/**/metadata).
# Historically only a fresh build lane pushed it; every Play *promotion* skipped metadata, so promoting
# an already-built binary after a listing change shipped a stale listing, and a raw `fastlane`/CI
# promote bypassed /idea-deploy's external drift gate entirely (user-flagged 2026-08-08:
# "it should sync on all states A1/A2/A3/A4, even on promote"). The heal: a shared, DRIFT-CHECKED
# listing sync baked into every store deploy lane (RULE-DEPLOY-LISTING-SYNC-ALL-STATES-001).
#
# This check asserts the wiring holds so the defect class cannot regress:
#   LS-1 the shared helper exists with the drift primitives + Play sync fn
#   LS-2 both Fastfiles import it (CI + local entry)
#   LS-3 every Play PROMOTION lane calls the drift-checked sync (the closed gap)
#   LS-4 iOS + macOS deploy lanes gate their metadata upload on the drift check (parity)
#   LS-5 the per-machine drift cache is gitignored
#   LS-6 the drift LOGIC is correct (never-synced → sync, unchanged → skip, changed → sync)
#
# exit 0 PASS · 1 FAIL. Runs on template AND forks — the deployment lanes are identical in both.
set -uo pipefail

# Resolve the repo root: HEALTH_ROOT when run via product-health.sh, else two levels up from here.
ROOT="${HEALTH_ROOT:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)}"
DEP="$ROOT/deployment"
HELPER="$DEP/_shared/listing_sync.rb"
fail=0

# ── LS-1 — shared helper present with the drift primitives + Play sync fn ─────
if [ ! -f "$HELPER" ]; then
  echo "❌ LS-1: $HELPER missing — the shared drift-checked listing sync helper must exist."
  fail=1
else
  for fn in "def store_listing_needs_sync?" "def record_store_listing_synced" "def _listing_sync_hash" "def sync_play_listing_if_changed"; do
    grep -qF "$fn" "$HELPER" || { echo "❌ LS-1: $HELPER missing '$fn'."; fail=1; }
  done
fi

# ── LS-2 — both Fastfiles import the helper (runs whichever fastlane loads) ───
for ff in "$DEP/Fastfile" "$DEP/fastlane/Fastfile"; do
  [ -f "$ff" ] || continue
  grep -q "listing_sync.rb" "$ff" || { echo "❌ LS-2: $ff does not import _shared/listing_sync.rb."; fail=1; }
done

# ── LS-3 — every Play PROMOTION lane calls the drift-checked sync (the gap) ───
for lane in android/play-beta android/play-production android/play-closed; do
  f="$DEP/$lane/lane.rb"
  [ -f "$f" ] || { echo "❌ LS-3: $f missing."; fail=1; continue; }
  grep -qF "sync_play_listing_if_changed" "$f" \
    || { echo "❌ LS-3: $lane/lane.rb does not call sync_play_listing_if_changed — a promotion would ship a stale listing."; fail=1; }
done

# ── LS-4 — iOS + macOS deploy lanes gate metadata on the drift check (parity) ─
grep -qE 'skip_metadata: *!?[a-z_]*listing_changed|store_listing_needs_sync\?\("ios"' "$DEP/ios/appstore/lane.rb" 2>/dev/null \
  || { echo "❌ LS-4: ios/appstore/lane.rb does not drift-gate its metadata upload."; fail=1; }
grep -qE 'skip_metadata: *!mac_listing_changed|store_listing_needs_sync\?\("mac"' "$DEP/desktop/mac-app-store/lane.rb" 2>/dev/null \
  || { echo "❌ LS-4: desktop/mac-app-store/lane.rb does not drift-gate its metadata upload."; fail=1; }

# ── LS-5 — the per-machine drift cache is gitignored ─────────────────────────
grep -q "deployment/fastlane/.listing_sync_state.json" "$ROOT/.gitignore" 2>/dev/null \
  || { echo "❌ LS-5: .gitignore does not exclude the drift cache (deployment/fastlane/.listing_sync_state.json)."; fail=1; }

# ── LS-6 — the drift LOGIC is correct (RED/GREEN, pure ruby) ─────────────────
if [ -f "$HELPER" ]; then
  tmp="$(mktemp -d)"
  mkdir -p "$tmp/deployment/android/metadata/en-US"
  ls6=$(ruby -e '
    DEPLOYMENT_REPO_ROOT = ARGV[0]; load ARGV[1]
    md = File.join(DEPLOYMENT_REPO_ROOT, "deployment/android/metadata")
    File.write(File.join(md, "en-US", "title.txt"), "A")
    ok = true
    ok &&= (store_listing_needs_sync?("android", md) == true)     # never-synced → sync
    record_store_listing_synced("android", md)
    ok &&= (store_listing_needs_sync?("android", md) == false)    # unchanged → skip
    File.write(File.join(md, "en-US", "title.txt"), "B")
    ok &&= (store_listing_needs_sync?("android", md) == true)     # changed → sync
    ok &&= (store_listing_needs_sync?("ios", File.join(DEPLOYMENT_REPO_ROOT,"nope")) == false)  # absent → no-op
    print(ok ? "PASS" : "FAIL")
  ' "$tmp" "$HELPER" 2>&1)
  rm -rf "$tmp"
  [ "$ls6" = "PASS" ] || { echo "❌ LS-6: drift-logic canary did not pass (got: $ls6)."; fail=1; }
fi

# ── LS-7 — CI promotion snippets materialize the listing (GHA end-to-end) ─────
# deployment/**/metadata is gitignored, so a promotion GHA job (which does NOT build) must run
# syncForkConfig itself before the drift-checked sync in the lane can push anything. Assert each
# no-build promotion snippet materializes the listing (syncForkConfig) so the GHA path syncs too.
for snip in android/play-beta android/play-closed android/play-production; do
  f="$DEP/$snip/workflow-snippet.yml"
  [ -f "$f" ] || continue
  grep -q "syncForkConfig" "$f" \
    || { echo "❌ LS-7: $snip/workflow-snippet.yml does not materialize the listing (syncForkConfig) — a GHA promote would ship a stale/empty listing."; fail=1; }
done

if [ "$fail" = 0 ]; then
  echo "✅ listing-sync: shared helper (LS-1) + both-Fastfile import (LS-2) + Play-promotion sync (LS-3) + iOS/mac drift-gate (LS-4) + gitignored cache (LS-5) + drift-logic (LS-6) + GHA-promote-materialize (LS-7) intact"
  exit 0
fi
exit 1
