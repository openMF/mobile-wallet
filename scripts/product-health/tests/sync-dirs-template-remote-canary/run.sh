#!/usr/bin/env bash
# run.sh — RED/GREEN canary for scripts/white-label/sync-dirs.sh TEMPLATE_REMOTE resolution.
#
# Heals two 2026-08-10 defects in the engine's remote block:
#   Finding-3 : resolution must MATCH the template by URL (a consumer's `upstream` may be its OWN repo),
#               not hardcode the `upstream` remote name.
#   Bug B     : `git remote add <template>` must NOT be gated on DRY_RUN=false — a --dry-run fetches the
#               template to compute the diff, so a fork with no template remote would fail at fetch.
#
#   GREEN: the real engine block → URL-match resolution present · remote-add UNGATED · 0 bare `upstream/` refs.
#   RED:   the old buggy block fixture (naive `upstream` + DRY_RUN-gated remote-add) → all three asserts fail.
set -uo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../../../.." && pwd)"
ENGINE="$REPO/scripts/white-label/sync-dirs.sh"

# Extract the TEMPLATE_REMOTE resolution block (marker to marker) from a file.
_block() { awk '/Resolve the TEMPLATE remote|No remote points at the template|TEMPLATE_REMOTE=""|OLD BUGGY BLOCK/{b=1} b{print} /Fetch from the template|^git fetch /{if(b)exit}' "$1"; }

# assert_ok <file> → 0 if all three properties hold, else 1 (prints failures)
assert_ok() {
  local f="$1" blk bad=0
  blk="$(_block "$f")"
  # (Finding 3) URL-match resolution: a normalizer + a loop comparing each remote's URL to the template URL.
  if grep -q '_norm_url' <<<"$blk" && grep -qE 'for _r in \$\(git remote\)' <<<"$blk"; then :; else echo "   ✗ no URL-match resolution (Finding 3)"; bad=1; fi
  # (Bug B) remote-add present AND not gated on DRY_RUN=false within the block.
  if grep -q 'git remote add' <<<"$blk"; then
    if grep -qE 'DRY_RUN.*=.*false' <<<"$blk"; then echo "   ✗ remote-add gated on DRY_RUN=false (Bug B)"; bad=1; fi
  else echo "   ✗ no 'git remote add' fallback in block"; bad=1; fi
  # (Finding 3) no bare name-based upstream remote hardcoded.
  if grep -qE 'TEMPLATE_REMOTE="upstream"|grep -q .\^upstream\$.' <<<"$blk"; then echo "   ✗ hardcoded name-based 'upstream' remote (Finding 3)"; bad=1; fi
  return "$bad"
}

rc_ok=0
echo "── GREEN (real engine) ──"
if assert_ok "$ENGINE"; then echo "   ✅ GREEN: URL-match · ungated remote-add · no naive upstream"; else echo "   ❌ GREEN failed"; rc_ok=1; fi

echo "── RED (old buggy block fixture) ──"
if assert_ok "$HERE/red/old-template-remote-block.sh"; then echo "   ❌ RED passed (canary blind to the old defect)"; rc_ok=1; else echo "   ✅ RED: old defects detected (as expected)"; fi

echo ""
[ "$rc_ok" -eq 0 ] && echo "canary: PASS" || echo "canary: FAIL"
exit "$rc_ok"
