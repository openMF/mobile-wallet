# Customization Surface — the fork-ownership contract

`customization-surface.yaml` (repo root) is the **single declared source of truth
for who owns each path** when a fork syncs template updates. It replaces ownership
knowledge that was previously implicit and scattered across `scripts/white-label/sync-dirs.sh`
(`SYNC_DIRS` + `EXCLUSIONS`), `scripts/white-label/customize.sh`, and the `syncForkConfig` copy map.

## Why it exists

A fork runs `scripts/white-label/sync-dirs.sh` to pull template updates while keeping its own branding
+ features. The hard question is *"which files may the sync overwrite?"* When that
answer lives only in an ad-hoc `EXCLUSIONS` list, a path with no exclusion gets
**blind-overwritten** — and that silently drops fork edits. The motivating case:

> `cmp-android/src/main/AndroidManifest.xml` is template-shipped **and**
> fork-extended (a fork adds `RECORD_AUDIO`, `FOREGROUND_SERVICE_MICROPHONE`, …).
> The `EXCLUSIONS` map preserved only `src/main/res`, so a full sync overwrote the
> manifest and **dropped the fork's permissions** — breaking the app.

The contract makes ownership **explicit, single-source, and machine-checkable**, so
that whole class of silent loss becomes impossible.

## The three ownership modes

| owner | meaning | sync behaviour |
|---|---|---|
| `template` | template is the sole author | **overwrite** the fork's copy. A fork edit here is drift — a genuine fix belongs **upstream** as a PR to `openMF/kmp-project-template`. |
| `fork` | the fork is the sole author | **never touch** it (branding, demo, the `core/store` seam, generated `Config.xcconfig`, local flavors, icons, store listings, fork identity). |
| `merge` | both author | **3-way merge**, never a blind copy (`AndroidManifest.xml`, `strings.xml`, `libs.versions.toml`, `settings.gradle.kts`, nav host). |

> The most-edited `owner: fork` files are the **white-label extension seams** — the registries a fork
> uses to add features, tabs, the home body, and startup hooks without touching template infra. They are
> documented in
> [`cmp-navigation/.../registry/README.md`](../../cmp-navigation/src/commonMain/kotlin/cmp/navigation/registry/README.md).

## Precedence

Rules are ordered **most-specific → most-general**, and the **first matching rule
wins**. So the narrow `merge`/`fork` carve-outs are declared *before* the broad
`template` module globs and correctly win over them. A trailing catch-all `**`
rule (`owner: fork`, `default: true`) means the runtime never clobbers an unknown
path — while `--verify` flags anything that reaches it so a human classifies it.

Glob syntax: `*` matches within one path segment; `**` matches across segments;
`**/x` matches `x` at any depth (including the repo root).

## The reader / validator — `scripts/customization-surface.sh`

Pure bash + awk (no `yq`/`jq`/`python` dependency — runs on any fork as-is).

```bash
# what owns this path?
scripts/customization-surface.sh resolve cmp-android/src/main/AndroidManifest.xml
#   → cmp-android/src/main/AndroidManifest.xml   merge  (strategy: manifest-union)

# owner of every tracked path
scripts/customization-surface.sh report

# CI canary — fail if any tracked path is unclassified (only matches the default)
scripts/customization-surface.sh verify
#   → ── customization-surface coverage: 1587/1587 classified ──
#   → ✅ every tracked path has an explicit owner
```

Source it as a library too:

```bash
source scripts/customization-surface.sh
cs_resolve_owner    "gradle/libs.versions.toml"   # → merge
cs_resolve_strategy "gradle/libs.versions.toml"   # → catalog-3way
```

## How `scripts/white-label/sync-dirs.sh` uses it

1. **Advisory report** — before the sync, `scripts/white-label/sync-dirs.sh` sources the reader and
   prints the `merge`-owned paths in the sync surface (fully guarded, cannot abort).
2. **3-way merge of `merge`-owned files** — after checking out the upstream copy of
   a directory, for every file that changed between the fork base and upstream and
   resolves to `owner: merge`, `scripts/white-label/sync-dirs.sh` runs `cs_merge <strategy> ours base
   theirs` instead of taking the blind upstream copy:
   - `ours` = fork's current file (`BASE_BRANCH`)
   - `theirs` = upstream file (`temp_branch`)
   - `base` = `git merge-base BASE_BRANCH temp_branch`

   `manifest-union` runs a **semantic** union (union `<uses-permission>` /
   `<uses-feature>` by `android:name`, keeping the template's structural update) so a
   fork's `RECORD_AUDIO` survives a template manifest change. The other strategies
   (`catalog-3way`, `include-union`, `kotlin-3way`, `strings-union`) run `git
   merge-file`, which cleanly unions non-overlapping edits and emits conflict markers
   **only on a true overlap** — surfaced with a `CONFLICT` warning for review, never
   silently shipped.

The `template`/`fork` mechanical behaviour (`SYNC_DIRS` + `EXCLUSIONS`) is unchanged;
the contract adds the `merge` path handling that previously didn't exist. Guarded so
forks that don't ship the reader are unaffected.

Proof: `tests/customization-surface/merge-3way-test.sh` — a fork's `RECORD_AUDIO` +
`FOREGROUND_SERVICE_MICROPHONE` survive a template update that adds
`POST_NOTIFICATIONS`, clean, no markers; a true same-line conflict returns rc=1 with
markers.

## Recommended CI wiring

Add a coverage gate so a new template file can never land unclassified (and thus
never silently clobber a fork later):

```yaml
- name: Verify customization-surface coverage
  run: bash scripts/customization-surface.sh verify
```

## Roadmap (follow-ups)

1. ~~Merge engine adoption~~ — **done**: `scripts/white-label/sync-dirs.sh` 3-way merges `merge`-owned
   files (`manifest-union` semantic + `git merge-file` for the rest). Remaining:
   fold the `fork`/`template` decisions into the same contract lookup and retire the
   hand-maintained `EXCLUSIONS` map (the reader can already answer `fork` → skip /
   `template` → copy; `EXCLUSIONS` becomes redundant).
2. **`syncForkConfig` alignment** — the plugin reads the same contract to know
   which generated files it owns vs must not clobber.

## Editing the contract

- A new **template-owned** module → add a `template` rule (and, if forks extend a
  specific file inside it, a narrower `merge`/`fork` rule *above* it).
- A new **fork-branded** path → add a `fork` rule.
- A file both sides edit → add a `merge` rule with a `strategy:`.
- Always run `scripts/customization-surface.sh verify` after editing.
