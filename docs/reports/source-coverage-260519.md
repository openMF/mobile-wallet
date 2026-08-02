# Source coverage audit — 2026-05-19

Baseline audit run after PR #153 (kover infrastructure) merged. Replaces the
earlier file-count proxy at `docs/reports/source-coverage-20260517.md`.

## Headline

| | |
|---|---|
| Kover infrastructure | ✅ wired (PR #153) — self-registering convention plugin, dynamic aggregation, typed-DSL reports config |
| Real per-module % numbers | ⏸ blocked on Gap #1 (see below) until a small follow-up PR lands |
| Test-fixture file-count inventory | ✅ this report — gives the prioritization order for Phase 9 T32-T36 |
| CI gate (T37) | Shipping in this same PR with floor = 0% per module (regression protection only; floors ratchet upward in subsequent PRs) |

## Gap #1 — `core:database` desktop compile broken on `dev`

Pre-existing on `origin/dev` HEAD `c4d38cd` (verified by checking out
`origin/dev` clean and running `./gradlew :core:database:compileProdDebugKotlinDesktop`).
NOT introduced by PR #153.

```
e: core/database/src/commonMain/.../DatabaseModule.kt:43:1
   Expected platformModule has no actual declaration in module <commonMain> for JVM
```

All five platform actuals (`androidMain`, `desktopMain`, `nativeMain`, `jsMain`,
`wasmJsMain`) exist with correct `actual val platformModule: Module` declarations,
but the kmp-product-flavors plugin appears to be introducing a `jvmCommon`
intermediate source set that the actual at `desktopMain` doesn't satisfy. Breaks
8+ flavored Desktop variants (`compileDemoReleaseKotlinDesktop`,
`compileProdReleaseKotlinDesktop`, `compileDemoStagingKotlinDesktop`, etc).

Because `koverXmlReport` triggers per-module compile + test up the dependency
graph, the broken compile halts the aggregated report before tests run anywhere
downstream. Partial per-module XMLs were written (13 modules) but all report
`0/N` — the artifacts are shells emitted before tests fired.

**Fix path**: separate small PR to either (a) add an explicit `jvmCommonMain`
intermediate actual, or (b) reshape the source-set hierarchy in core/database's
build.gradle.kts. This audit doesn't block on it because the file-count
inventory below is sufficient to prioritize the Phase 9 T32-T36 PR sequence.

This is exactly the kind of regression a coverage-floor CI gate would have
caught — and is the strongest argument for landing T37 now.

## Test-fixture inventory

Counted via `find <module>/src -name '*.kt' [-path '*Test*' or not]` on the
`dev` working tree. Module path on the left, source-Kotlin count, test-Kotlin
count.

### `feature/*` (all current pattern exemplars)

| Module | src .kt | test .kt | Plan exemplar | Phase 9 task |
|---|---:|---:|---|---|
| `feature/crypto` | 6 | **0** | Pattern A (CoinDetail) + Pattern B (CryptoWatchlist) | T32 |
| `feature/currency-rates` | 6 | **0** | Pattern C (RateHistory) | T33 |
| `feature/emi-calculator` | 4 | **0** | Pattern E (UseCase + input) | T34 |
| `feature/home` | 3 | 0 | — | — |
| `feature/profile` | 2 | 0 | — | — |
| `feature/settings` | 13 | **0** | Pattern H (static / local) | T35 |
| `feature/create-loan` | — | — | Pattern F (input-draft-resilient) — **NONE_YET** | T36 (author new) |
| `feature/search-clients` | — | — | Pattern G (search with debounced query) — **NONE_YET** | T36 (author new) |

Every existing feature module ships with zero tests. The plan's named missing
fixtures (`feature/crypto/CoinDetailViewModelTest.kt`, `FakeCryptoRepository.kt`)
are confirmed absent.

### `core/*`

| Module | src .kt | test .kt |
|---|---:|---:|
| `core/analytics` | 5 | 0 |
| `core/common` | 2 | 0 |
| `core/data` | 30 | 0 |
| `core/database` | 29 | 12 |
| `core/datastore` | 3 | 0 |
| `core/designsystem` | 8 | 0 |
| `core/domain` | 1 | 0 |
| `core/model` | 11 | 0 |
| `core/network` | 8 | 0 |
| `core/store` | 4 | 0 |
| `core/ui` | 13 | 0 |

`core/database` is the only `core/*` module with any tests (12 files) — and
it's the one currently broken on Desktop compile.

### `core-base/*` (framework-shared layer)

| Module | src .kt | test .kt |
|---|---:|---:|
| `core-base/analytics` | 16 | 1 |
| `core-base/common` | 12 | 0 |
| `core-base/database` | 5 | 0 |
| `core-base/datastore` | 10 | 0 |
| `core-base/designsystem` | 26 | 0 |
| `core-base/network` | 9 | 0 |
| `core-base/platform` | 22 | 0 |
| `core-base/security` | 51 | 7 |
| **`core-base/store`** | **27** | **26** | ← gold-standard exemplar
| `core-base/ui` | 34 | 4 |

`core-base/store` is the canonical "what 100% test coverage on a Pattern
exemplar looks like" — outcome of PRs #145 / #147 / #148. Everything else in
this layer needs the Phase 9 push.

## Prioritization for Phase 9 PRs

Recommended ordering, smallest-blast-radius first:

1. **Gap #1 fix** (~50 LOC, 1 file) — unblock the real-numbers audit
2. **T32 `feature/crypto/`** — smallest feature module (6 src files); plan
   already names the missing fixtures (CoinDetailViewModelTest +
   FakeCryptoRepository)
3. **T33 `feature/currency-rates/`** — 6 src files, similar shape to crypto
4. **T34 `feature/emi-calculator/`** — 4 src files, Pattern E input flow
5. **T35 `feature/settings/`** — 13 src files (largest existing feature)
6. **T36 (new exemplars)** `feature/create-loan/` + `feature/search-clients/`
   — author from scratch, biggest scope
7. **`core/*` rollup** — 11 modules; can be grouped by domain
8. **`core-base/*` rollup** — already partial; close the remaining gaps

CI floor (T37) ratchets upward after each PR — each task adds ~5-10
percentage points to that module's floor entry in `.kover-floor.yml`.

## Out-of-band findings

- `CombineScreenStatesNArityTest.kt:53` fails on JS / wasmJs targets with a
  test-name error (surfaced during prior Desktop test compilation). Not a
  Phase-9 blocker — affects only the non-JVM kover paths, which kover doesn't
  instrument anyway. File a separate issue.
