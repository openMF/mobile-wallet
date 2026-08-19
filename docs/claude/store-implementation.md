# Store Implementation Guide

> How the offline-first data pipeline works and how to extend it in your fork.

---

## Architecture Overview

```
Fetcher (network) ──► Store5 ──► SourceOfTruth (Room)
                          │
                          ▼
                    StoreData<T>  ◄──  asScreenStream()
                          │
                    DecisionEngine.decide()
                          │
                    ScreenState<T>  ──►  Screen Composable
```

Every screen-data flow goes through three layers:

1. **Store5** — in-memory + Room cache; handles network/cache coordination
2. **`asScreenStream()`** — converts `StoreResponse` flow to `Flow<ScreenState<T>>`
3. **`DecisionEngine`** — pure function mapping `StoreData + NetworkStatus → ScreenState`

---

## Screen State Types

`ScreenState<T>` has six variants:

| Variant | When |
|---|---|
| `Loading` | No cached data yet, waiting for first fetch |
| `Content(data, freshness, fetchedAt)` | Data available (FRESH / STALE / UPDATING) |
| `NoNetwork` | Offline and no cached data; `isCaptivePortal` flag for portal detection |
| `Error` | Non-network error with no cached data |
| `Empty` | Fetch succeeded, server returned nothing |
| `Unauthenticated` | Auth error — prompt re-login |

`DataFreshness` inside `Content`:
- `FRESH` — successfully fetched from network
- `STALE` — cached data shown while offline or after a fetch error
- `UPDATING` — background refresh in progress (show spinner overlay)

---

## Read-Only Streams

### Single-entity / list screen
```kotlin
// ViewModel
val uiState: StateFlow<ScreenState<List<Loan>>> = store
    .asScreenStream(StoreRequest.cached(key, refresh = true))
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScreenState.Loading)

// Screen
ScreenContent(uiState) { loans -> LoanList(loans) }
```

### Paginated screen
```kotlin
val pager = Pager(PagingConfig(pageSize = 20)) {
    StorePagingSource(store, keyFactory = { page -> LoanPageKey(page) })
}
val pagingFlow = pager.flow.cachedIn(viewModelScope)

// Screen
PagingScreenContent(pagingFlow) { loan -> LoanItem(loan) }
```

### Load-once (reference data, user profile)
```kotlin
val uiState = loadOnceStream(
    fetch = { apiService.getProfile() },
    cache = { profileDao.observe() },
).stateIn(...)
```

---

## Fetch Policy

`FetchPolicy` controls whether the Store hits network on each request:

| Policy | Behaviour |
|---|---|
| `CACHE_ONLY` | Never fetch; emit stale data or `NoNetwork` |
| `NETWORK_ONLY` | Always fetch; ignore cache |
| `NETWORK_WITH_CACHE` | Emit cached immediately, then refresh (default) |

Pass via `StoreRequest`:
```kotlin
store.asScreenStream(StoreRequest.cached(key, refresh = policy == NETWORK_WITH_CACHE))
```

### NETWORK_WITH_CACHE

Use `FetchPolicy.NETWORK_WITH_CACHE` for any domain with a Room SourceOfTruth + Fetcher.
Internal routing (from `StoreDataExtensions.kt`):
- Fresh cache (within TTL, `validator.isValid == true`) → emit cache immediately + background refresh
- Stale or empty cache → try network first; on failure fall back to cache

### createOfflineStore — OFFLINE_LOCAL_ONLY

`StoreFactory.createOfflineStore(sourceOfTruth)` wraps `StoreBuilder.from(sourceOfTruth)` with no Fetcher.
Use when the app is the single source of truth (alerts, loans, bill reminders).

### SpotRateLookupStore — NETWORK_ONLY + Room write side-effect

`createStore(fetcher=frankfurterApi, sourceOfTruth=exchangeRatesDao)` with `FetchPolicy.NETWORK_ONLY`
at the stream callsite. Read path always skips cache (always-fresh). Write path persists to Room
as a side-effect — so `NETWORK_WITH_CACHE` consumers (currency list) pick up the fresh rate
without their own network call.

---

## Mutation / Input Submission

> Screen-archetype name in `ui.yaml` (when used with `/kmp-feature` codegen): `type: input`.
> The "form" terminology here refers to the *API parameter names* (e.g. `formKey`,
> `observePendingByFormKey`) and shape of user input — the screen archetype itself is
> called `input` (covers form / wizard / quick-action / confirm / gesture).

> **Unified write view-model.** Screens extend the single `BaseMutationViewModel<T, R>`
> parameterized by **`MutationMode`** — `MutationMode.InSession` (single-shot submit) or
> `MutationMode.Draft` (offline draft, 3-case resume). An earlier design split this into two base
> VMs; the mode now expresses the difference. The `SubmitHandler` / `DraftSubmitHandler` shown below
> are the lower-level handlers the base VM composes.

> **Read shape is picked by `store_archetype`.** The generator routes on
> `feature_profile.store_archetype` (8 archetypes) to select the `core/store` factory — see
> [STORE_DATA_API.md](../architecture/STORE_DATA_API.md) and `FEATURE_AUTHORING.md`.


### Basic mutation (no draft persistence)
```kotlin
class CreateLoanHandler(
    private val api: LoanApi,
    private val loanStore: Store<LoanKey, Loan>,
) : SubmitHandler<CreateLoanRequest, Loan> {

    override suspend fun submit(payload: CreateLoanRequest): Result<Loan> =
        runCatching { api.createLoan(payload) }
            .onSuccess { loanStore.clear() }  // invalidate cache
}
```

### Draft-preserving mutation (offline-resilient)

Use `DraftSubmitHandler` when you want to persist form payloads across app restarts:

```kotlin
class CreateLoanDraftHandler(
    private val api: LoanApi,
    private val draftDao: DraftDao,
    private val loanStore: Store<LoanKey, Loan>,
) : DraftSubmitHandler<CreateLoanRequest, Loan>(
    formKey = "create_loan",
    draftDao = draftDao,
) {
    override suspend fun doSubmit(payload: CreateLoanRequest): Result<Loan> =
        runCatching { api.createLoan(payload) }
            .onSuccess { loanStore.clear() }
}
```

The framework will:
- Save the draft as PENDING on first call
- Transition it SUBMITTED on success / FAILED on network error
- Re-surface PENDING drafts on the next app launch (via `observePendingByFormKey`)

### Mutation Screen wiring
```kotlin
MutationScreenContent(
    uiState = viewModel.submitState,
    onSubmit = viewModel::submit,
) { formState ->
    CreateLoanForm(formState)
}
```

---

## Cache Lifecycle

### On logout — clear all caches
```kotlin
// In your AuthRepository or logout use-case
storeCacheManager.clearAll()
```

`clearAll()` wipes:
- All registered Store5 in-memory caches and SourceOfTruth (Room) rows
- Bookkeeper sync-failure records
- All draft rows in `framework_submit_drafts`

### On app start — prune expired drafts
```kotlin
// In your app initializer or startup WorkManager task
storeCacheManager.pruneExpiredDrafts()  // default 30-day TTL
```

Only SUBMITTED and FAILED rows are pruned. PENDING drafts are never removed — the
user may still want to resume an unsent form.

Custom TTL:
```kotlin
storeCacheManager.pruneExpiredDrafts(maxAgeMs = 7L * 24 * 60 * 60 * 1000)  // 7 days
```

### Registering your Store for logout clearing
```kotlin
// In your feature's Koin DI module
single<StoreCacheManager> { get<StoreCacheManagerImpl>() }

// Register each store that should be cleared on logout
(get<StoreCacheManager>() as StoreCacheManagerImpl).register(get(AppStoreRegistry.LoanStore))
```

---

## Combining Multiple Streams

```kotlin
// Two independent stores → single combined ScreenState
val uiState: Flow<ScreenState<DashboardData>> = combineScreenStates(
    loansFlow,
    savingsFlow,
) { loans, savings -> DashboardData(loans, savings) }
```

Priority when combining: `NoNetwork > Loading > Error > Content`.

---

## Room Invalidation Bridge — `core-base/database/invalidation/`

> **TL;DR for feature authors:** when your repository writes to a Room table, wrap the
> write with `notifyingWrite("my_table") { dao.upsert(...) }`. When your repository
> exposes a `Flow<T>` backed by a Room DAO, wrap the read with
> `daoFlow("my_table") { dao.observeXxx() }`. That's it.

### Why this is here

Room 3 alpha05's `InvalidationTracker.refreshAsync()` schedules its post-write fan-out
via `database.getCoroutineScope().launch { ... }`. On Android/Desktop/iOS this is fine
because real parallel threads run the launched refresh concurrently with the writer.
On **wasmJs** there is one thread (the JS event loop): the launched refresh is queued
on the same task list as Compose recomposition, your ViewModel's StateFlow updates,
and the worker's message round-trips. Two failure modes follow:

1. **Starvation** — by the time the refresh actually completes, the user has navigated
   away; the original Flow collector's downstream `combine(...)` has already emitted
   stale state.
2. **`pendingRefresh` AtomicBoolean stuck** — if the launched refresh hasn't finished,
   subsequent `refreshAsync()` calls become no-ops, silently dropping a write's signal.

Net effect on wasmJs: long-lived Flow collectors (a Home dashboard's `combine{}` that
the user keeps mounted across navigation) stop refreshing after writes. A fresh-mounted
screen still sees the latest data via Room's `createFlow(emitInitialState = true)`
initial query, so the bug masquerades as "Bills screen works, Home stays stale."

### The three primitives

The bridge lives in `core-base/database/src/commonMain/kotlin/kpt/core/base/database/invalidation/`:

| Primitive | Use for |
|---|---|
| `RoomChangeBus.notify("my_table")` | Manual signal publishing when you can't use `notifyingWrite{}` (e.g. raw `db.useWriterConnection { ... }`) |
| `daoFlow("my_table") { dao.observeXxx() }` | Wrap a Room DAO `Flow` so it re-emits on matching writes |
| `notifyingWrite("my_table") { dao.upsert(...) }` | Wrap a write so the bus is notified iff the block succeeds |

The bus is a process-wide singleton; no lifecycle, no DI. The wraps work on every
platform — on Android/Desktop/iOS they run alongside Room's own InvalidationTracker
at microsecond cost; on wasmJs they are the reliable propagation path.

### Integration recipe — new features

When you add a feature with its own Room entity:

1. **Pick the table name(s).** Use the same string Room's `@Entity(tableName = "…")`
   uses. Multi-table writes (joins, transactions touching multiple tables) pass them
   all: `notifyingWrite("a", "b") { ... }`, `daoFlow("a", "b") { ... }`.
2. **Wrap every write.** Every `dao.insert/update/upsert/delete` in the repository goes
   inside `notifyingWrite("my_table") { ... }`.
3. **Wrap every read.** Every `dao.observeXxx()`-returning method in the repository
   wraps with `daoFlow("my_table") { ... }`. Wrap the Store5 SourceOfTruth reader too:
   `reader = { _: Unit -> daoFlow("my_table") { dao.observeAll() } }`.

### Reference impls

- `core/store/.../banking/impl/BillRemindersStore.kt` + `LoansStore.kt` — Store5 wiring
- `core/data/.../banking/impl/BillReminderRepositoryImpl.kt` + `LoanRepositoryImpl.kt` —
  repository wiring with `notifyingWrite{}` on writes and `daoFlow{}` on direct-DAO reads
- `core-base/database/.../invalidation/README.md` — full rationale, design notes, and
  the removal plan for when Room 3 stable lands (codemod-friendly).

### When **not** to use

- Reads that aren't backed by a Room `Flow` (network flows, `MutableStateFlow` you
  mutate yourself, derived flows from a `StateFlow<DomainModel>`).
- Cross-tab / cross-process invalidation — Room 3 alpha05 has no multi-instance support
  on web; neither does this bridge.

### Removal

When Room 3 stable ships, the three primitives can be converted to no-op pass-throughs
(deprecated, codemod-friendly), then the call sites can be removed across the repo with
a mechanical search-replace. No `feature/*` changes needed. Full plan in the README.

---

## Customisation Seam — `core/store/`

**DO NOT** modify `core-base/store` — it upgrades cleanly across template versions.

Everything consumer-forks need lives in `core/store/`:

| File | Purpose |
|---|---|
| `AppScreenStateDefaults.kt` | Brand visuals, copy, Lottie animations, telemetry hooks |
| `AppErrorMapper.kt` | Domain-error → user-message mapping (extends `categorize()`) |
| `AppStoreRegistry.kt` | Named Store qualifiers (Koin) |
| `appStoreModule.kt` | Koin DI bindings for your Store factories |

`KptTheme` provides `LocalScreenStateDefaults` automatically — zero per-screen wiring.

---

## DefaultValidator wiring note

If you pass a `DefaultValidator` to `StoreFactory.createStore()`, you **must** call
`validator.markFresh()` inside the Fetcher block after every successful network fetch.
Failing to do so causes every response to be treated as stale, re-triggering network
fetches on every collection.

```kotlin
StoreFactory.createStore(
    fetcher = Fetcher.of { key ->
        val result = api.fetch(key)
        validator.markFresh(key)   // <-- required
        result
    },
    validator = validator,
    ...
)
```
