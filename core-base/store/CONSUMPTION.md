# Consuming `core-base/store` (from `core/store`)

> The Store5 factory + infra primitives underneath `core/store`. A fork never calls `StoreFactory`
> directly when adding a feature — that's `core/store`'s `provide<Name>Store` job (see
> `core/store/CONSUMPTION.md`). This is the lower-level contract: which primitive backs which
> archetype, and what `core/store` wires around it.

## Call sequence

1. `core/store`'s `provide<Name>Store(...)` picks a `StoreFactory` factory by
   `feature_profile.store_archetype`: `createStore(fetcher, sourceOfTruth, validator?, memoryPolicy?)`
   for `NETWORK_WITH_CACHE`, `createOfflineStore(sourceOfTruth)` for `OFFLINE_LOCAL_ONLY`,
   `createMemoryStore(fetcher)` for `MEMORY_ONLY`, `createMutableStore(fetcher, sourceOfTruth,
   converter, updater, bookkeeper)` for `MUTABLE`.
2. For `MUTABLE`, the `bookkeeper: Bookkeeper<Key>` parameter is the shipped `RoomBookkeeper`
   (`core-base/store/infra/impl`), backed by `BookkeeperDao` / `BookkeeperEntity` from
   `core-base/database`. It persists sync-failure timestamps across process restarts so a
   `MutableStore` can retry offline writes on reconnect.
3. `core/store` registers the built `Store` / `MutableStore` in Koin under a qualifier from its own
   `AppStoreRegistry`, which extends this module's `StoreRegistry` (`protected fun store(name) =
   named(name)`) — `core-base` supplies only the qualifier-naming mechanism; the qualifiers themselves
   are fork-owned.
4. This module's own `StoreModule` Koin module is intentionally near-empty — `StoreFactory` is a plain
   `object` with nothing to bind. The real per-feature bindings live in `core/store`'s own DI module
   (`appStoreModule`).
5. The read side turns any `Store<Key, Output>` into a `ScreenDataStream<Output>` via
   `Store<Key, Output>.asScreenStream(key, networkMonitor, fetchedAtRepository, cacheKey, scope,
   fetchPolicy = ...)` — `core/data` repositories call this, not the feature layer.
6. On logout call `StoreCacheManager.clearAll()`; on app start call `pruneExpiredDrafts()` (30-day
   default TTL for SUBMITTED/FAILED draft rows — PENDING drafts are never pruned).

## Notes

- `StoreFactory.createScreenWithMutation(...)` fuses a read `Store` and a write `SubmitHandler` into
  one `ScreenWithMutationStream` for screens that both display and edit the same record — optional;
  most screens compose the read stream and `SubmitHandler` separately instead.
- Framework-owned: `StoreFactory`, `DecisionEngine`, and the `infra/impl` Room-backed defaults
  (`RoomBookkeeper`, `RoomFetchedAtRepository`, `StoreCacheManagerImpl`). Fork pressure goes to
  `core/store`'s `provide*Store` functions and `AppStoreRegistry`, never here — see the
  "framework-shared, don't modify" note in the root `CLAUDE.md`.

Canonical example: `core/store/CONSUMPTION.md` is the feature-facing contract this module backs.

Symbols: StoreFactory, RoomBookkeeper, StoreRegistry, StoreCacheManager, asScreenStream, createScreenWithMutation
