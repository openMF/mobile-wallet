#!/usr/bin/env bash
# =============================================================================
# scripts/secrets/generate-manifest.sh — regenerate the root secrets-manifest.yaml
# =============================================================================
# Per-target deployment/<platform>/<target>/secrets-needs.yaml is the SOURCE OF
# TRUTH. This aggregates their vault_aliases into the root secrets-manifest.yaml
# (the alias list the framework's vault flow resolves). Edit needs files, then
# run this — never hand-edit the generated manifest's `needs:` block.
#
#   scripts/secrets/generate-manifest.sh            # rewrite secrets-manifest.yaml
#   scripts/secrets/generate-manifest.sh --check    # CI: fail if out of date
# =============================================================================
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"; . "$DIR/_lib.sh"
MANIFEST="$REPO_ROOT/secrets-manifest.yaml"

render() {
  cat <<'HDR'
# =============================================================================
# secrets-manifest.yaml — GENERATED, DO NOT EDIT BY HAND
# =============================================================================
# Regenerate with: scripts/secrets/generate-manifest.sh
# Source of truth:  deployment/<platform>/<target>/secrets-needs.yaml#vault_aliases
#
# Each needs[].alias resolves through the framework (one-way, workspace only):
#   alias → core/registries/SECRETS_ALIAS_REGISTRY.yaml → (vault, secret_id)
#         → SOPS+age decrypt → materialize.at path
# OSS forkers IGNORE this file and run scripts/secrets/setup-secrets.sh instead.
# Authority: RULE-SECRETS-VAULT-001, RULE-SECRETS-ALIAS-REGISTRY-001 (SAR10)
# =============================================================================
schema_version: "3.0"
project: mifos-x/kmp-project-template
default_account_email: null

needs:
HDR
  while IFS=$'\t' read -r alias canon plats; do
    [ -n "$alias" ] || continue
    echo "  - alias: $alias"
    echo "    platforms: [${plats//,/, }]"
    # materialize.at lands under secrets/live/ — the resolver root (secrets/LAYOUT.yaml
    # roots.live) — so a Path-B (vault) pull writes where build-secrets reads. env: paths
    # already resolve to secrets/live/ via secrets_env_local_file; file paths get the prefix here.
    case "$canon" in
      env:*) at="$(secrets_env_local_file "${canon#env:}")"
             echo "    materialize: { at: \"$at\", mode: env-line, env_var: ${canon#env:} }" ;;
      *)     at="${canon/secrets\//secrets/live/}"
             echo "    materialize: { at: \"$at\", mode: file, permissions: \"0600\" }" ;;
    esac
  done < <(secrets_all_vault_aliases)
}

if [ "${1:-}" = "--check" ]; then
  if diff -q <(render) "$MANIFEST" >/dev/null 2>&1; then
    echo "secrets-manifest.yaml up to date"; exit 0
  else
    echo "secrets-manifest.yaml OUT OF DATE — run scripts/secrets/generate-manifest.sh" >&2; exit 1
  fi
fi
render > "$MANIFEST"
echo "wrote $MANIFEST ($(grep -c '^  - alias:' "$MANIFEST") aliases)"
