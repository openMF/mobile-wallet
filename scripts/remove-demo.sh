#!/usr/bin/env bash
#
# remove-demo.sh — strip the kmp-project-template demo showcase, leaving a clean fork.
#
# Invoked by `customizer.sh --clean`. Convention (showcase-framework-separation epic):
#   • demo domain code lives under **/demo/** packages,
#   • demo feature modules are include()d inside a `// demo:begin … // demo:end` block
#     in settings.gradle.kts,
#   • demo entries in central files (@Database, Koin modules, store registry) are wrapped
#     in `// demo:begin … // demo:end` markers.
#
# Removal = strip every marked block + delete every **/demo/** package + delete the demo
# feature modules + reset the DB schema to a fresh-fork baseline + swap the demo-coupled
# app shell (home dashboard + nav) for a minimal placeholder.
#
# Usage:
#   remove-demo.sh                 # DRY RUN (default) — report what would change, touch nothing
#   remove-demo.sh --apply         # actually perform the removal
#   remove-demo.sh --apply --all   # explicit; --all is the only supported scope today
#
set -euo pipefail

APPLY=0
FORMAT=1
for arg in "$@"; do
  case "$arg" in
    --apply) APPLY=1 ;;
    --all)   : ;;                    # only supported scope today; per-feature is a later enhancement
    --dry-run) APPLY=0 ;;
    --no-format) FORMAT=0 ;;         # skip closing spotlessApply (fast iteration)
    *) echo "remove-demo.sh: unknown arg '$arg'" >&2; exit 2 ;;
  esac
done

# repo root = parent of scripts/
cd "$(dirname "$0")/.."

MODE=$([ "$APPLY" -eq 1 ] && echo apply || echo dry-run)
say() { if [ "$APPLY" -eq 1 ]; then echo "  $*"; else echo "  [dry-run] would $*"; fi; }

# ── 1. Demo feature names (parsed from the settings demo-block BEFORE it is stripped) ──
DEMO_FEATURES=$(awk '/demo:begin/{s=1;next} /demo:end/{s=0} s' settings.gradle.kts \
                  | grep -oE 'feature:[a-z-]+' | sed 's/feature://' || true)

# ── 2. Marked files to strip (convention-discovered) ──────────────────────────────────
MARKED_FILES=$(grep -rl 'demo:begin' --include='*.kt' --include='*.kts' . 2>/dev/null | grep -v '/build/' || true)

echo "remove-demo ($MODE): demo features = ${DEMO_FEATURES//$'\n'/ }"

# ── 3. Strip every `// demo:begin … // demo:end` block ────────────────────────────────
echo "strip marked blocks:"
while IFS= read -r f; do
  [ -z "$f" ] && continue
  say "strip demo blocks in ${f#./}"
  if [ "$APPLY" -eq 1 ]; then
    awk '/demo:begin/{skip=1} /demo:end/{skip=0; next} !skip' "$f" > "$f.tmp" && mv "$f.tmp" "$f"
  fi
done <<< "$MARKED_FILES"

# ── 3b. Remove dangling demo imports left in surviving files by the block strip ───────
#     (an `import kpt.core.<m>.demo.…` or `import kpt.feature.<demoFeature>.…` whose target
#      was just deleted is an unresolved-reference compile error, not a warning — drop them.)
echo "remove dangling demo imports:"
DEMO_PKGS=$(echo "$DEMO_FEATURES" | tr -d '-' | paste -sd'|' -)
[ -z "$DEMO_PKGS" ] && DEMO_PKGS="__none__"
say "strip 'import kpt.core.*.demo.*' + demo-feature imports from surviving .kt files"
if [ "$APPLY" -eq 1 ]; then
  find . -name '*.kt' -not -path '*/build/*' -print0 | while IFS= read -r -d '' f; do
    sed -i '' -E \
      -e '/^import kpt\.core\.[a-z]+\.demo\./d' \
      -e '/^import kpt\.feature\.[a-z]+\.demo\./d' \
      -e "/^import kpt\\.feature\\.(${DEMO_PKGS})[.]/d" \
      "$f"
  done
fi

# ── 4. Delete every **/demo/** package directory under core/ ──────────────────────────
echo "delete demo packages:"
while IFS= read -r d; do
  [ -z "$d" ] && continue
  say "rm -rf ${d#./}"
  [ "$APPLY" -eq 1 ] && rm -rf "$d"
done < <(find core feature -type d -name demo -not -path '*/build/*' 2>/dev/null)

# ── 5. Delete demo feature modules ────────────────────────────────────────────────────
echo "delete demo feature modules:"
while IFS= read -r feat; do
  [ -z "$feat" ] && continue
  say "rm -rf feature/$feat"
  [ "$APPLY" -eq 1 ] && rm -rf "feature/$feat"
done <<< "$DEMO_FEATURES"

# ── 6. Reset the Room schema VERSION to a fresh-fork baseline ─────────────────────────
#     (the @AutoMigration history lived inside the stripped demo block; a fresh fork has
#      no installed users to migrate, so it starts clean at v1).
DB="core/database/src/commonMain/kotlin/kpt/core/database/AppDatabase.kt"
if [ -f "$DB" ]; then
  say "reset $DB VERSION → 1"
  [ "$APPLY" -eq 1 ] && sed -i '' 's/const val VERSION = [0-9][0-9]*/const val VERSION = 1/' "$DB"
fi

# ── 7. Formatter pass to drop the now-unused demo imports left by the block strip ─────
#     The app shell (home dashboard + nav) needs NO swap: the demo dashboard lives under
#     feature/home/demo/ (deleted in step 4) and the framework HomeScreen shell + nav files
#     carry `// demo:begin … // demo:end` blocks (stripped in step 3). The stripped shell —
#     top bar + settings entry point + empty home body — compiles as-is for the fork to fill.
if [ "$APPLY" -eq 1 ] && [ "$FORMAT" -eq 1 ]; then
  say "run ./gradlew spotlessApply (drops now-unused imports left by the strip)"
  ./gradlew spotlessApply --console=plain -q || true
else
  say "spotless SKIPPED (--no-format or dry-run)"
fi

echo "remove-demo: done ($MODE)."
