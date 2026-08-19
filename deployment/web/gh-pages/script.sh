#!/usr/bin/env bash
# deployment/web/gh-pages/script.sh
# Tier-1 web target — GitHub Pages.
# Builds the Kotlin/JS browser distribution and stages it for actions/deploy-pages.
set -euo pipefail

# 1. Build the JS browser distribution.
./gradlew :cmp-web:jsBrowserDistribution

# 2. Stage. actions/deploy-pages (CI) uploads from cmp-web/build/dist/js/productionExecutable/.
# Local-only: optionally push to gh-pages branch.
DIST="cmp-web/build/dist/js/productionExecutable"
[[ -d "$DIST" ]] || { echo "Web dist not found at $DIST"; exit 1; }

if [[ -n "${LOCAL_GH_PAGES_PUSH:-}" ]]; then
  npx gh-pages -d "$DIST" --message "deploy: $(git rev-parse --short HEAD)"
else
  echo "Built web dist at $DIST. CI uses actions/deploy-pages from this directory."
fi
