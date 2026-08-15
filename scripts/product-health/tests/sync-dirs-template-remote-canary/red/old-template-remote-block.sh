# ── OLD BUGGY BLOCK (pre-2026-08-10) — kept as a RED fixture; do NOT ship this shape ──
# Finding-3 defect: name-based `upstream` (a consumer's own repo), not URL-matched to the template.
TEMPLATE_REMOTE="upstream"
if ! git remote | grep -q '^upstream$'; then
    # Bug B defect: remote-add gated on DRY_RUN=false, but the fetch below runs unconditionally.
    if [ "$DRY_RUN" = false ]; then
        git remote add upstream "$DEFAULT_UPSTREAM_URL"
    fi
fi
# Fetch from the template
git fetch "$TEMPLATE_REMOTE"
