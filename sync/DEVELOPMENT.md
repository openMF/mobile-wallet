# `sync/` — DEVELOPMENT

Internal-contributor guide to the offline-write / background-sync worker infrastructure, built on
[worker-kmp](https://github.com/MobileByteLabs/worker-kmp) + [KMPNotifier](https://github.com/mirzemehdi/KMPNotifier).
For how a **fork** consumes this module (the `WorkScheduler` façade, the `@WorkerKmpWorkers`
codegen contract), see [`README.md`](README.md) — it already documents the consumer-facing sync
flow end-to-end and is not duplicated here.

## Purpose

`sync/` is the **integration layer** between `core/data`/`core/datastore` (the `Synchronizer`/
`Syncable` ports + `SyncStatePersister`) and worker-kmp's cross-platform `WorkManager`. It ships
two `CoroutineWorker`s (`DataSyncWorker`, `NotificationWorker`), a thin scheduling façade
(`WorkScheduler`/`DefaultWorkScheduler`), and the local-notification wiring
(`SyncNotifier.kt` + per-platform `syncNotifierConfiguration` actuals).

## Build & test the module itself

```bash
./gradlew :sync:test                                    # commonTest suite
./gradlew :sync:test --tests "*DefaultWorkSchedulerTest"
./gradlew :sync:check                                     # + detekt/spotless
```

Source sets: `commonMain` (all worker/scheduler logic) + `androidMain`, `desktopMain`, `iosMain`,
`jsMain`, `wasmJsMain` (each contributes only the one-line `syncNotifierConfiguration` actual for
`SyncNotifier.kt` — everything else, including both workers, is common). Only `commonTest` exists
today (`DefaultWorkSchedulerTest.kt`) — it fakes `WorkManager` directly (`FakeWorkManager`) rather
than pulling in worker-kmp's test fixtures, to pin the delegation contract
`LoanReminderUseCase` (in `feature/loans`) relies on. Follow that pattern — a fake `WorkManager`
implementing the interface — for new scheduler-level tests; `DataSyncWorker`/`NotificationWorker`
are exercised indirectly today (no dedicated worker unit test yet — a real gap to fill if you touch
`doWork()`).

## Internal architecture & key contracts

- **`WorkScheduler`** (`WorkScheduler.kt`) is the only public surface consumer feature modules see
  — 4 methods: `enqueueDataSync`, `scheduleNotification`, `observeWork`, `cancelWork` (+
  `cancelWorkByTag`). Consumers never import `io.github.mobilebytelabs.worker.*` directly; that
  decoupling means worker-kmp API churn only touches `DefaultWorkScheduler`.
- **`DefaultWorkScheduler`** (`infra/DefaultWorkScheduler.kt`) encodes the project's scheduling
  policy: data sync is unique-named (`DATA_SYNC_WORK_NAME`) under `ExistingWorkPolicy.KEEP` (silent
  no-op if already in flight); notifications are one-shot with `initialDelay`, optionally
  unique-named under `REPLACE` (re-scheduling a bill after edit); `WorkMode.Foreground` requests
  Android expedited execution via `OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST` (no-op on
  other platforms).
- **`DataSyncWorker`** (`infra/DataSyncWorker.kt`) implements `Synchronizer` itself (in-memory
  `ChangeListVersions`, read from `SyncStatePersister` at start, written back on success) and fans
  out to its `Syncable` collaborators via an **explicit `awaitAll`** — deliberately **no
  `getAll<Syncable>()` runtime discovery**. Partial failure is full failure
  (`Result.retry()`); this matches the single `Flow<Boolean>` shape NiA-style dashboards expect.
- **`NotificationWorker`** (`infra/NotificationWorker.kt`) reads `title`/`body`/`channelId` from
  `inputData` and calls `renderNotification` (`SyncNotifier.kt`), backed by KMPNotifier's
  Firebase-free `kmpnotifier-local` module.
- **`SyncModule`** (`di/SyncModule.kt`) binds only `WorkScheduler` — sync-status *observation* is
  provided by the `cmp-worker-sync` library's `SyncObserverKoinModule`/`UniqueWorkObserver`
  (`includes(SyncObserverKoinModule)`), not hand-rolled here.
- **The forced-refresh seam is Store5-native**: both `Syncable` adopters force a re-fetch via
  `store.stream(StoreReadRequest.fresh(key)).first { it is Data || it is Error }` — a one-shot
  cold collect that terminates, unlike `asScreenStream` (which launches perpetual coroutines into
  its `scope` and would hang the worker). This is why the integration ships **zero edits to
  `core-base/store/**`** — see `README.md`'s "The forced-refresh seam" section for the full
  rationale.
- **Codegen boundary**: the worker registry + Koin injection are generated from the
  `@WorkerKmpWorkers` annotation in `cmp-shared/WorkerDeclarations.kt` — this module never
  hand-registers workers into a map.

## How to extend/modify safely

- **Adding a third `Syncable` adopter** (the module's primary extension point — see `README.md`
  §"How to add a third `Syncable` adopter" for the full 4-step recipe): declare `: Syncable` on the
  repository interface, implement `syncWith` via `synchronizer.snapshotSync(name) { ... }`, add the
  repository as a `DataSyncWorker` constructor parameter, add one `async { repo.syncWith(this@DataSyncWorker) }`
  line to `doWork()`. Nothing else — Koin auto-wires the new constructor param via
  `@WorkerKmpWorkers` codegen. **Three is the documented natural cap** before this should be
  refactored to a registry pattern instead of an explicit constructor list — if you're adding a
  fourth, stop and reconsider the shape first.
- **Adding a new platform notification actual**: mirror `SyncNotifier.android.kt` — only
  `syncNotifierConfiguration()` needs a per-platform body; `renderNotification`/`initSyncNotifier`
  stay in `commonMain`. Android is the only platform with real rendering wired
  (`NotificationPlatformConfiguration.Android` + a `kpt.sync.notifications` channel) — other
  platforms fall back to KMPNotifier library defaults.
- **Retry/backoff contract**: `DataSyncWorker.doWork()` returns `WorkResult.retry()` on any
  `Syncable` failure (caught via `runCatching` around the whole `awaitAll`), delegating actual
  backoff scheduling to worker-kmp's `WorkManager`/`CoroutineWorker` — this module does not
  implement its own backoff timer. Don't swallow a partial failure to force `success()`; that
  breaks the "partial failure is full failure" invariant the sync flow depends on.
- **`WorkScheduler` is append-only from the consumer's point of view** — if you need a new
  capability, add a method to the interface (and implement in `DefaultWorkScheduler`) rather than
  widening an existing method's semantics, since `LoanReminderUseCase` and future consumers pin
  behavior against the current 5 methods.
- Real background scheduling on iOS/Desktop/Web and real Android notification-permission handling
  (`POST_NOTIFICATIONS` runtime permission, `NotificationManagerCompat`) are explicitly out of
  scope at v1 (see `README.md` §"Out of scope") — don't assume they're wired if you're building on
  top of this module for a new platform target.

## Gotchas

- `api(projects.core.data)` and `api(libs.worker.kmp)` are `api` (not `implementation`) **on
  purpose** — `DataSyncWorker`'s public constructor exposes `core/data` repository types, and
  `:sync`'s worker classes publicly extend `CoroutineWorker`; the `@WorkerKmpWorkers` KSP codegen
  in `cmp-shared` needs both to resolve transitively. Downgrading either to `implementation` breaks
  KSP param/supertype resolution in the consuming module, not in this one — the failure surfaces
  far from the cause.
- `observeWork(handle)` looks work up **by tag** (`handle.uniqueName ?: NOTIFICATION_WORK_NAME_PREFIX`)
  then filters by `id` — if you add a new enqueue path, make sure it tags the request consistently
  or `observeWork` will never find it.
- The `sync` module applies `id("org.convention.worker.compose")` (see
  `../build-logic/convention/src/main/kotlin/WorkerComposeConventionPlugin.kt`), which
  programmatically applies the worker-kmp Gradle app plugin and opts its `workerKmpAppCodegen*`
  tasks out of configuration cache (a documented upstream worker-kmp limitation) — don't
  re-enable config-cache for those tasks locally without checking the plugin's comment first.
