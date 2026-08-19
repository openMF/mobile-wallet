#!/usr/bin/env bash
# legal/_shared/config.sh
#
# Reads gradle/libs.versions.toml (runtime SoT) + gradle/fork.properties
# (fork-specific values) and exports all variables consumed by the templates.
#
# Usage: source legal/_shared/config.sh
# Then:  envsubst "${LEGAL_VARS}" < template.md > output.md

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# ── Read helpers ──────────────────────────────────────────────────────────────

_toml() {
  grep -E "^\s*$1\s*=" "$REPO_ROOT/gradle/libs.versions.toml" \
    | head -1 | grep -oE '"[^"]+"' | head -1 | tr -d '"'
}

_prop() {
  local key="$1"
  local props="$REPO_ROOT/gradle/fork.properties"
  [[ -f "$props" ]] || return 0
  grep -E "^${key}=" "$props" 2>/dev/null | head -1 | cut -d= -f2-
}

# ── App identity (from libs.versions.toml — never hardcode these) ─────────────

export APP_NAME="${APP_NAME:-$(_toml appDisplayName)}"
export APP_ID="${APP_ID:-$(_toml appId)}"
export APP_PROJECT_NAME="${APP_PROJECT_NAME:-$(_toml projectName)}"

# ── Organization (from fork.properties → ENV override for CI) ─────────────────

export ORG_NAME="${ORG_NAME:-$(_prop org.name)}"
export ORG_EMAIL="${ORG_EMAIL:-$(_prop org.email)}"
export APP_WEBSITE="${APP_WEBSITE:-$(_prop org.marketing.url)}"
export APP_SUPPORT_URL="${APP_SUPPORT_URL:-$(_prop org.support.url)}"

# ── Legal-specific (from fork.properties → ENV override for CI) ───────────────

export COMPANY_NAME="${COMPANY_NAME:-$(_prop legal.company.name)}"
export JURISDICTION="${JURISDICTION:-$(_prop legal.jurisdiction)}"
export EFFECTIVE_DATE="${EFFECTIVE_DATE:-$(_prop legal.effective.date)}"
export LEGAL_CONTACT_EMAIL="${LEGAL_CONTACT_EMAIL:-$(_prop legal.contact.email)}"

# ── Fallbacks: legal fields fall back to org fields if not set ────────────────

export COMPANY_NAME="${COMPANY_NAME:-${ORG_NAME}}"
export LEGAL_CONTACT_EMAIL="${LEGAL_CONTACT_EMAIL:-${ORG_EMAIL}}"

# ── Privacy policy URL (auto-derived from repo if not explicitly set) ─────────
# Forks can override via fork.properties org.privacy.url or ENV.
# Default: raw GitHub URL pointing to the committed output file.

_PRIVACY_PROP="$(_prop org.privacy.url)"
_SUPPORT_PROP="$(_prop org.support.url)"
export PRIVACY_POLICY_URL="${PRIVACY_POLICY_URL:-${_PRIVACY_PROP}}"
export SUPPORT_URL="${SUPPORT_URL:-${_SUPPORT_PROP:-${APP_WEBSITE}}}"

# ── Explicit variable list for envsubst (prevents substituting unrelated $vars) ─

export LEGAL_VARS="\
\${APP_NAME}\
\${APP_ID}\
\${APP_PROJECT_NAME}\
\${COMPANY_NAME}\
\${ORG_NAME}\
\${JURISDICTION}\
\${EFFECTIVE_DATE}\
\${LEGAL_CONTACT_EMAIL}\
\${APP_WEBSITE}\
\${SUPPORT_URL}\
\${PRIVACY_POLICY_URL}\
"
