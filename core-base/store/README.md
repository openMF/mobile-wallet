# core-base/store

Framework-shared, offline-first **read + write + sync seam** for every screen in the
template. Wraps [Store5](https://github.com/MobileNativeFoundation/Store) + the
network monitor into a uniform `ScreenState` / `SubmitState` / `CombinedState`
contract — your ViewModel exposes one `StateFlow` and the screen renders against
one value.

> **Do NOT modify this module in your fork.** It's framework-shared and upgrades
> cleanly across template versions. Push fork pressure to `core/store` instead —
> see `core/store/README.md` for the brand-the-visuals seam.

---

## What you get for free

When a ViewModel uses `Store.asScreenStream(...)` or `StoreFactory.createScreenWithMutation(...)`,
the framework handles **every** transition the screen could need:

- Initial **Loading** until the first emission arrives
- **NoNetwork** when offline + no cached data (including captive-portal detection)
- **Unauthenticated** on HTTP 401/403 (routes the user to login)
- **Error** with `isNetworkError` classification (for retry vs. dismiss UX)
- **Content** with a freshness signal — `FRESH`, `STALE`, or `UPDATING`
- **Empty** when fetch completed with zero items
- Auto-refresh on network reconnect (300 ms debounced, prevents WiFi↔Cell flicker)
- Pull-to-refresh / retry debounce (1 s default, prevents tap-spam thrashing)
- Persisted "last fetched at" timestamp via `FetchedAtRepository` (survives ViewModel
  destruction and process restart when backed by Room)
- Last-known content preserved during refresh — UI never bounces through `Loading`
  when the user pulls to refresh

The mapping `(StoreData, NetworkStatus) → ScreenState` lives in `DecisionEngine` —
a pure, exhaustively unit-testable function. No per-ViewModel `when` blocks.

---

## Read patterns

| Pattern                           | API                                       | Use for                                                              |
|-----------------------------------|-------------------------------------------|----------------------------------------------------------------------|
| Continuous single-key             | `Store.asScreenStream(key, ...)`          | Detail screens, dashboards — keep streaming as data changes          |
| Continuous dynamic-key            | `Store.asScreenStream(keyFlow, ...)`      | "Selected client" pattern — key changes drive a re-stream            |
| Load-once (edit form preload)     | `Store.asLoadOnceStream(key, ...)`        | Edit screens — fetch initial value, then stop so edits aren't reset  |
| Paginated infinite scroll         | `Store.asPagingScreenStream(...)`         | Lists with `loadNextPage()` — manages page state + load-more footer  |
| Raw `StoreData` (no DecisionEngine)| `Store.streamData(key)`                  | Repository internals, multi-source combines, custom dispatch         |
| **Read + write + sync (fused)**   | `StoreFactory.createScreenWithMutation(…)` | Edit/in-place-update screens — one `StateFlow<CombinedState<R, W>>` |

> The fused read + write + sync seam (`ScreenWithMutationStream`) is **new in
> Phase 01 (2026-05-27)** — see [Combined read + write + sync](#combined-read--write--sync)
> below.

---

## FetchPolicy

`FetchPolicy` is a sealed interface — pass to any read API to override default
cache-then-network behaviour.

| Policy                              | Wire-level request                                 | Use for                                                       |
|-------------------------------------|----------------------------------------------------|---------------------------------------------------------------|
| `CACHE_THEN_NETWORK` (default)      | `StoreReadRequest.cached(key, refresh = true)`     | Most screens — cached data immediately, refresh in background |
| `NETWORK_THEN_CACHE_FALLBACK` *new* | `StoreReadRequest.fresh(key, fallback = true)`     | Online-first; gracefully use SoT when network fails           |
| `NETWORK_ONLY`                      | `StoreReadRequest.fresh(key, fallback = true)`     | Never-stale data (payment status, balance after txn)          |
| `CACHE_ONLY`                        | `StoreReadRequest.localOnly(key)`                  | Offline-only views, never hits the network                    |

> `NETWORK_THEN_CACHE_FALLBACK` and `NETWORK_ONLY` issue the same Store5 request
> shape — the distinction is documentary. `NETWORK_ONLY` callers should treat
> cache-fallback as an unexpected degraded state and surface it explicitly;
> `NETWORK_THEN_CACHE_FALLBACK` callers treat it as the expected happy path
> during transient network failure.

Policy → request mapping lives in [`StoreDataExtensions.streamDataForPolicy()`].
DecisionEngine is **not** policy-aware — the case mapping is purely on
`(StoreData, NetworkStatus)`; policy difference manifests at the stream layer.

---

## Write patterns

| Pattern                          | API                                             | Use for                                                            |
|----------------------------------|-------------------------------------------------|--------------------------------------------------------------------|
| One-shot submission              | `SubmitHandler<R>` (via `scope.submitHandler()`) | Forms, action buttons, confirms — one API call per submit         |
| Offline-resilient submission     | `DraftSubmitHandler<P, R>`                       | Forms that survive crashes / no-net — drafts persist to outbox    |
| Background outbox drain          | `OfflineSubmitSyncer`                           | App-start + connectivity-up triggers — retries the outbox queue   |

Submit state machine lives in `SubmitState` — sealed interface with `Idle / Submitting
/ Submitted<R> / Failed(error, category)`. Compose helpers (`MutationScreenContent`,
`SubmitProgressOverlay`, `SubmitResultHandler`) ship in `core-base/ui`.

---

## Combined read + write + sync

`ScreenWithMutationStream<R, W>` fuses a read-side `ScreenState<R>` with a
write-side `SubmitState<W>` plus optional outbox-pending / sync-status flows
into a single hot `StateFlow<CombinedState<R, W>>`. Drives edit screens that
both display data and submit mutations against the same domain object.

```kotlin
private val stream = StoreFactory.createScreenWithMutation(
    store = loanStore,
    key = loanId,
    networkMonitor = networkMonitor,
    fetchedAtRepository = fetchedAtRepo,
    cacheKey = "loans:detail:$loanId",
    submitHandler = viewModelScope.submitHandler<LoanForm>(),
    submitBlock = { form -> loanRepository.update(loanId, form); form },
    scope = viewModelScope,
    fetchPolicy = FetchPolicy.CACHE_THEN_NETWORK,
    pendingCountFlow = billsOutbox.observeAllByFormKey("loan:$loanId").map { it.size },
)
val state: StateFlow<CombinedState<Loan, LoanForm>> = stream.state

fun onSave(form: LoanForm) = stream.submit(form)
fun onRetry() = stream.refresh()
fun onDismissError() = stream.cancelPendingSubmit()
```

`CombinedState` fields:

- `read: ScreenState<R>` — current read-side state (Loading / Content / NoNetwork / …)
- `mutation: SubmitState<W>` — current write-side state (Idle / Submitting / Submitted / Failed)
- `outboxPending: Int` — number of pending offline mutations (badge counter)
- `isSyncing: Boolean` — background drain in progress (separate from `mutation.isSubmitting`)

> **Phase 01 status:** the contract + default impl + `StoreFactory` factory are
> shipped. Rolling out to existing edit ViewModels (e.g.
> `feature/bills/BillsAddViewModel`) is a follow-up sweep — consumers can opt
> in incrementally.

Future phases add a freshness-indicator Compose surface (Phase 03) under
`core/designsystem/component/freshness/` — read-side `ScreenState.Content.fetchedAt`
already carries the data this UI needs.

---

## Factory APIs

`StoreFactory` is the single discoverable entry point:

| Factory                            | Returns                              | Notes                                                  |
|------------------------------------|--------------------------------------|--------------------------------------------------------|
| `createStore`                      | `Store<Key, Output>`                 | Network + SourceOfTruth                                |
| `createMemoryStore`                | `Store<Key, Output>`                 | Fetcher-only, in-memory cache                          |
| `createMutableStore`               | `MutableStore<Key, Output>`          | Adds writer + bookkeeper for offline sync              |
| **`createScreenWithMutation`**     | `ScreenWithMutationStream<R, W>`     | Phase 01 — fused read + write + sync seam              |

---

## Related modules

- `core-base/store/submit/` — `SubmitHandler`, `DraftSubmitHandler`,
  `SubmitOutbox`, `OfflineSubmitSyncer`
- `core-base/store/screen/` — `ScreenDataStream`, `LoadOnceStream`, `ScreenState`,
  `StoreData`, `FetchPolicy`
- `core-base/store/paging/` — `PagingScreenStream`, `StorePagingSource`,
  `PageKey`
- `core-base/store/infra/` — `StoreFactory`, `DecisionEngine`,
  `FetchedAtRepository`, `StoreRegistry`, `DefaultValidator`
- `core-base/store/combine/` — `CombinedState`, `ScreenWithMutationStream`
  *(Phase 01)*
- `core-base/store/error/` — `ErrorCategory`, `categorize()`, `OfflineException`
- `core-base/ui/` — `ScreenContent`, `PagingScreenContent`, `MutationScreenContent`
  composables that consume the state machines above

See the **screen-archetype taxonomy table** in
[`core/store/README.md`](../../core/store/README.md) for screen-type ↔ API
mapping (used by `/kmp-feature` codegen via `ui.yaml.screens[].type`).

---

## When to reach for what

| Screen archetype                    | Read API                          | Write API                              | Compose wrapper          |
|-------------------------------------|-----------------------------------|----------------------------------------|--------------------------|
| Detail / dashboard                  | `asScreenStream`                  | —                                      | `ScreenContent`          |
| Edit form (continuous read)         | `asLoadOnceStream` + `SubmitHandler` *or* `createScreenWithMutation` | `SubmitHandler` / `DraftSubmitHandler` | `MutationScreenContent`  |
| Paginated list                      | `asPagingScreenStream`            | —                                      | `PagingScreenContent`    |
| Multi-source combine                | `combineScreenStates(s1, s2, ...)`| —                                      | `ScreenContent`          |
| Quick action / confirm              | —                                 | `SubmitHandler<Unit>`                  | `MutationScreenContent`  |
| Offline-resilient form              | —                                 | `DraftSubmitHandler<P, R>`             | `MutationScreenContent`  |
