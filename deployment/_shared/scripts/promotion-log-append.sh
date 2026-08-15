#!/usr/bin/env bash
# deployment/_shared/scripts/promotion-log-append.sh
#
# Append a single row to the append-only promotion log (#events[]) per
# RULE-DEPLOYMENT-MANIFEST-001 DM6 (12-field schema). The log is FRAMEWORK-level state (E1/D-4):
# the internal `/idea-deploy` path exports PROMOTION_LOG_PATH → the project's
# deployment-layer/deploy-state/PROMOTION_LOG.yaml and consumes the SAME deployment/ infra. A community
# GitHub Actions deploy leaves PROMOTION_LOG_PATH UNSET → this is a clean no-op (its audit trail is the
# Actions run history; nothing is committed into the public source repo).
#
# Usage:
#   promotion-log-append.sh \
#     --platform <android|ios|mac|desktop|web> \
#     --lane <lane-name>                       \
#     [--stage <prerelease|beta|stable|production|staging|preview>] \
#     [--tag <git-tag>]                        \
#     --actor <github-actor>                   \
#     --run-id <github-run-id>
#
# All fields normalize into the canonical 12-field schema at promotion-log.schema.json.
set -euo pipefail

PLATFORM=""; LANE=""; STAGE=""; TAG=""; ACTOR=""; RUN_ID=""

while [ $# -gt 0 ]; do
  case "$1" in
    --platform) shift; PLATFORM="${1:-}" ;;
    --lane)     shift; LANE="${1:-}"     ;;
    --stage)    shift; STAGE="${1:-}"    ;;
    --tag)      shift; TAG="${1:-}"      ;;
    --actor)    shift; ACTOR="${1:-}"    ;;
    --run-id)   shift; RUN_ID="${1:-}"   ;;
    *)          echo "Unknown arg: $1" >&2; exit 2 ;;
  esac
  shift || true
done

[[ -n "$PLATFORM" ]] || { echo "--platform required" >&2; exit 2; }
[[ -n "$LANE"     ]] || LANE="(none)"
[[ -n "$ACTOR"    ]] || ACTOR="ci-system"
[[ -n "$RUN_ID"   ]] || RUN_ID="local"

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
MANIFEST="$REPO_ROOT/deployment/DEPLOYMENT_MANIFEST.yaml"

# Framework-level log path, exported by /idea-deploy. Unset (community GitHub Actions) → no-op.
LOG="${PROMOTION_LOG_PATH:-}"
if [ -z "$LOG" ]; then
  echo "note: PROMOTION_LOG_PATH unset — deploy audit stays with the CI run (no committed log)" >&2; exit 0
fi
mkdir -p "$(dirname "$LOG")"
[[ -f "$LOG" ]] || printf '# PROMOTION_LOG.yaml — append-only deploy audit (framework deployment-layer)\nevents:\n' > "$LOG"

TS="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
SHA="$(git -C "$REPO_ROOT" rev-parse HEAD 2>/dev/null || echo unknown)"
MANIFEST_SHA="$(git -C "$REPO_ROOT" hash-object "$MANIFEST" 2>/dev/null || echo unknown)"
CI_RUN_URL="local"
if [[ -n "${GITHUB_REPOSITORY:-}" && "$RUN_ID" != "local" ]]; then
  CI_RUN_URL="https://github.com/${GITHUB_REPOSITORY}/actions/runs/${RUN_ID}"
fi

# Canonical target name = "{platform}-{lane}[-stage]" (matches DEPLOYMENT_MANIFEST.canonical_name shape).
TARGET="${PLATFORM}-${LANE}"
[[ -n "$STAGE" ]] && TARGET="${TARGET}-${STAGE}"

# Determine tier from manifest (best-effort grep — manifest is human-edited).
TIER="$(awk -v t="$TARGET" '
  $0 ~ "canonical_name: "t {found=1}
  found && /tier:/ {sub(/^.*tier: */, ""); print $0; exit}
' "$MANIFEST" 2>/dev/null || echo 1)"
[[ "$TIER" =~ ^[12]$ ]] || TIER=1

cat >> "$LOG" <<EOF

  - timestamp:    "$TS"
    actor:        "$ACTOR"
    target:       "$TARGET"
    tier:         $TIER
    version_from: "none"
    version_to:   "${TAG:-auto}"
    commit_sha:   "$SHA"
    ci_run_url:   "$CI_RUN_URL"
    manifest_sha: "$MANIFEST_SHA"
    secrets_sha:  "unknown"
    mode:         "confirm"
    secrets_mode: "${SECRETS_MODE:-vault}"
    outcome:      "success"
EOF

echo "📒 Appended PROMOTION_LOG row: $TARGET @ $TS"
