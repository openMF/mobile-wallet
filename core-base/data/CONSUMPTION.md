# Consuming `core-base/data` in a fork

> Framework-shared infrastructure contracts — connectivity, timezone, and the sync algorithm —
> that `core/data` repositories and the `sync/` worker build on. Not a repository layer itself;
> nothing here is fork-branded, so a fork consumes it as-is rather than extending it.

## Call sequence

1. **Inject `NetworkMonitor`** (bound `single<NetworkMonitor> { NetworkMonitorProvider.install() }`
   in `core/data`'s `RepositoryModule`) into any repository that needs `store.asScreenStream(...)`'s
   `networkMonitor` parameter, or read `isOnline` / `networkStatus` directly for a connectivity
   banner. Substituting your own implementation requires passing `NetworkMonitorContractTest`
   against it — invariants (StateFlow, synchronous initial value, debounce bounds) are documented
   on `NetworkMonitorContract`.
2. **Inject `TimeZoneMonitor`** wherever a screen needs to react to the device timezone changing
   (e.g. re-render a schedule). Bound per-platform in `core/data`'s `PlatformModule`
   (`PlatformDependentDataModule.android.kt` / `PlatformModule` non-Android actual).
3. **Make a repository `Syncable`** to participate in a forced background refresh: implement
   `syncWith(synchronizer): Boolean`, calling `synchronizer.snapshotSync(name) { ... }` for a
   snapshot API (re-collect one `StoreReadRequest.fresh(key)` emission) — see
   `MacroIndicatorsRepositoryImpl` / `CurrencyRepositoryImpl`. Use `changeListSync` instead if your
   backend exposes a delta `?since=N` endpoint.
4. **Wire the `Syncable` into the worker** — `sync/`'s `DataSyncWorker` implements `Synchronizer`
   (reading/writing `ChangeListVersions` via `SyncStatePersister` from `core-base/datastore`) and
   constructor-injects each `Syncable` repository explicitly; there is no runtime `Syncable`
   discovery, so adding one means editing `DataSyncWorker`'s constructor + `SyncModule`.

## Notes

- `SyncManager` is a legacy observer contract (pre-`cmp-worker-sync`); the shipped `sync/` module's
  `SyncModule` now binds connectivity-aware sync observation through `cmp-worker-sync`'s
  `SyncObserverKoinModule` (`UniqueWorkObserver`) instead. Keep `SyncManager` in mind only if you're
  rolling your own scheduling integration outside `cmp-worker-sync`.
- Never hardcode a `NetworkMonitor`/`TimeZoneMonitor` platform check in a repository — inject the
  interface and let the per-platform actual (or `cmp-network-monitor`) do the platform branching.
- This module is framework-shared (E2/T3) — push fork-specific sync logic to `core/data` /
  `sync/`, not here.

Canonical example: `core/data`'s `MacroIndicatorsRepository` (World Bank, snapshot sync) and
`CurrencyRepository` (Frankfurter, snapshot sync); `sync/`'s `DataSyncWorker`.

Symbols: NetworkMonitor, NetworkMonitorContract, TimeZoneMonitor, Synchronizer, Syncable, NetworkChange, changeListSync, snapshotSync, SyncManager
