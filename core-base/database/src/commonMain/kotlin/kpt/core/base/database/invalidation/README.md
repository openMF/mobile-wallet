# Room Invalidation Bridge

Framework-level infrastructure that absorbs the wasmJs invalidation gap in Room 3 alpha05.
Three primitives — `RoomChangeBus`, `daoFlow {}`, `notifyingWrite {}` — give every DAO
Flow consumer a deterministic, parallelism-independent re-emission signal after writes,
without changing Room itself or touching any `feature/*` code.

> **TL;DR for feature authors:** if your repository writes to a Room table, wrap the write
> with `notifyingWrite("my_table") { dao.upsert(...) }`. If your repository exposes a
> `Flow<T>` backed by a Room DAO, wrap the read with `daoFlow("my_table") { dao.observeXxx() }`.
> That's it.

---

## Why this exists

The Room 3 alpha05 stream design works on Android/Desktop/iOS by relying on **real
parallel threads** — `Dispatchers.IO` runs the post-write `invalidationTracker.refreshAsync()`
on a separate thread, concurrent with the writer's continuation, and the launched
coroutine completes its tracker-table SELECT before the user does anything else.

On **wasmJs** there is one thread (the JS event loop). `Dispatchers.Default` and
`Dispatchers.Main` are the same thread. The launched refresh coroutine is queued on
the same task list as Compose recomposition, your ViewModel's StateFlow updates, your
navigation events, and the worker's message round-trips. Two specific failure modes
emerge:

1. **Starvation.** Each step of `notifyInvalidation()` suspends (acquiring the connection,
   sending a worker message, waiting for the response, reading the tracker table). Each
   suspension yields to other queued tasks. By the time the refresh actually completes,
   the user may have already navigated and the original `Flow` collector's downstream
   `combine(...)` has already emitted a stale state.
2. **`pendingRefresh` AtomicBoolean stuck.** `InvalidationTracker.refreshAsync()` gates on
   `pendingRefresh.compareAndSet(false, true)`. If the previously-launched refresh hasn't
   finished, subsequent calls become no-ops. On Android this almost never happens because
   the launched coroutine is fast; on wasmJs where everything serializes on one event
   loop, two writes in rapid succession can lose one refresh entirely.

This bridge **does not fight Room** — it runs alongside the existing InvalidationTracker.
On Android/Desktop/iOS the bridge is a microsecond-cost no-op (a `SharedFlow.tryEmit`
with no collectors observing those tables). On wasmJs it is the reliable propagation
path until Room 3 stable lands.

---

## The three primitives

### 1. `RoomChangeBus` — the bus

```kotlin
public object RoomChangeBus {
    public val signal: SharedFlow<Set<String>>     // tables that changed
    public fun notify(table: String)               // publish single-table signal
    public fun notify(tables: Set<String>)         // publish multi-table signal
}
```

Process-wide singleton. No lifecycle. Backed by a buffered `MutableSharedFlow` with
`DROP_OLDEST` overflow policy (sustained bursts coalesce; final state is never missed
because each signal carries the table name and downstream re-querying is idempotent).

### 2. `daoFlow {}` — read wrapper

```kotlin
public fun <T> daoFlow(vararg tables: String, block: () -> Flow<T>): Flow<T>
```

Wrap any Room DAO `Flow` so it re-queries on matching bus signals. Initial subscribe
emits immediately so the first query runs without waiting for a write.

### 3. `notifyingWrite {}` — write wrapper

```kotlin
public suspend inline fun <T> notifyingWrite(
    vararg tables: String,
    crossinline block: suspend () -> T,
): T
```

Wrap any Room DAO write so the bus is notified iff the block completes successfully.
If `block` throws, no signal — collectors aren't woken to re-query unchanged state.

---

## Integration recipe — existing features

Three call-site patterns, all 1-line edits, no signature changes, no DI wiring:

| Site type | Before | After |
|---|---|---|
| **Write in a repo** | `dao.upsert(entity)` | `notifyingWrite("my_table") { dao.upsert(entity) }` |
| **Direct-DAO `Flow` read in a repo** | `dao.observeXxx().map { ... }` | `daoFlow("my_table") { dao.observeXxx() }.map { ... }` |
| **Store5 `SourceOfTruth.reader`** | `reader = { _: Unit -> dao.observeAll() }` | `reader = { _: Unit -> daoFlow("my_table") { dao.observeAll() } }` |

The shipping repos under `core/data/banking/` and the shipping stores under
`core/store/banking/` follow this exact pattern. Use them as references.

---

## Integration recipe — new features

When you add a new feature with its own Room entity:

1. **Pick the table name(s).** Use the same string Room's `@Entity(tableName = "…")` uses.
2. **Wrap every write.** Every `dao.insert/update/upsert/delete` in the repository goes
   inside `notifyingWrite("my_table") { ... }`. Multi-table transactions pass all touched
   tables: `notifyingWrite("a", "b") { ... }`.
3. **Wrap every read.** Every `dao.observeXxx()`-returning method in the repository
   wraps with `daoFlow("my_table") { ... }`. If the read joins two tables, pass both.

That's the contract. Three calls per new feature. No more.

---

## What this does **not** cover

- **Reads not backed by a Room `Flow`.** If your repo returns a `Flow<T>` constructed by
  hand (e.g. a `MutableStateFlow` you mutate yourself, or a network Flow), the bus is
  irrelevant — wrap the bus signal yourself via `RoomChangeBus.signal.flatMapLatest { ... }`
  if needed.
- **Writes through `db.useWriterConnection` raw.** If you bypass DAOs and write to the
  database directly via `useWriterConnection { ... }`, call `RoomChangeBus.notify(...)`
  manually after the block returns. There is no driver-level interception.
- **Cross-process / cross-tab invalidation.** Room 3 alpha05 has no multi-instance
  support on web yet; neither does this bridge. If your app opens multiple browser
  tabs, each tab has its own `RoomChangeBus` instance and writes in one tab won't
  notify subscribers in another. Out of scope for the template's current shape.

---

## Removal plan (when Room 3 stable lands)

Validation criterion: ≥ 2 weeks of `dev`-branch usage on Room 3 stable where wasmJs
home dashboards refresh correctly without the bridge active.

### Step 1 — neuter the primitives (1 PR, mechanical)

Convert each body to a no-op pass-through:

```kotlin
public object RoomChangeBus {
    public val signal: SharedFlow<Set<String>> = MutableSharedFlow<Set<String>>().asSharedFlow()
    public fun notify(table: String): Unit { /* no-op */ }
    public fun notify(tables: Set<String>): Unit { /* no-op */ }
}

@Deprecated("Room 3 stable handles invalidation — remove call sites at your convenience",
            ReplaceWith("block()"))
public fun <T> daoFlow(vararg tables: String, block: () -> Flow<T>): Flow<T> = block()

@Deprecated("Room 3 stable handles invalidation — remove call sites at your convenience")
public suspend inline fun <T> notifyingWrite(
    vararg tables: String,
    crossinline block: suspend () -> T,
): T = block()
```

Behavior identical to "bus does nothing" — Room's own InvalidationTracker handles all
fan-out. The deprecated annotations let IDEs guide consumer apps through the codemod.

### Step 2 — codemod the call sites (1 PR, mechanical)

Across the repo:

- `notifyingWrite("...") { x }` → `x`
- `daoFlow("...") { x }` → `x`

Roughly 30 sites in the shipping template. Forks have their own count.

### Step 3 — delete the package

Delete `core-base/database/src/commonMain/kotlin/kpt/core/base/database/invalidation/`
entirely. No remaining references after step 2.

---

## Related rules

- `core/store/README.md` — Store5 archetypes; `createOfflineStore` is the primary
  integration point at the SourceOfTruth layer.
- `docs/claude/store-implementation.md` — feature-author recipe with end-to-end
  examples covering this bridge.

## Tracking

This bridge exists because of [androidx Room 3 alpha05 + WebWorkerSQLiteDriver
invalidation propagation on wasmJs]. Track Room's release notes; when alpha06+ /
beta / stable announces "InvalidationTracker reliable on web", run the removal
plan above.
