#!/usr/bin/env bash
# scripts/ci/verify-workflow-token-scope.sh
#
# Pre-push gate for `.github/workflows/*` edits — enforces AC-11 of
# deploy-gha-product-flavors (Phase 5 authored; Phase 6 executes).
#
# WHY THIS EXISTS
# ---------------
# GitHub silently rejects pushes that modify files under `.github/workflows/`
# when the pushing token does NOT carry the `workflow` OAuth scope. The
# rejection is at the API layer, AFTER local `git push` reports success on
# the object upload — the actual ref update fails with an obscure 422 that
# most contributors misread as a transient network hiccup. This gate makes
# the failure visible BEFORE the push so the fix (`gh auth refresh -s
# workflow`) lands seconds instead of minutes into the debug loop.
#
# WHEN TO RUN IT
# --------------
# Before any `git push` from a branch whose diff touches
# `.github/workflows/*`. `/git-session-commit` and the framework's
# `git-session-*` runtimes are expected to invoke this script from the
# workflow-scope preflight step (Phase 6 wires the dispatch).
#
# CONTRACT
# --------
# Exit 0 → token carries `workflow` scope. Safe to push.
# Exit 1 → token missing `workflow` scope. Fix hint printed. Push refused.
#
# INVARIANT
# ---------
# Never echo the token value. Never log the token to a file. `gh auth status`
# writes scope metadata to stderr in its "Token scopes:" section — we grep
# that section only.

set -euo pipefail

# gh writes auth status to STDERR — merge and grep. `head -1` keeps the first
# match (multi-host `gh` installations list one line per host).
SCOPE=$(gh auth status 2>&1 | grep -o 'workflow' | head -1 || true)

if [[ -z "$SCOPE" ]]; then
  echo "❌ HALT — gh token missing 'workflow' OAuth scope."
  echo ""
  echo "   Pushing changes under .github/workflows/* will be silently rejected"
  echo "   by the GitHub API. Fix by re-authenticating with the workflow scope:"
  echo ""
  echo "     gh auth refresh -s workflow"
  echo ""
  echo "   Then re-run this script; only after it exits 0 push your workflow"
  echo "   edits."
  exit 1
fi

echo "✅ gh token carries 'workflow' scope — safe to push .github/workflows/* edits"
