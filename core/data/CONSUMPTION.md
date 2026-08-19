# Consuming `core/data` in a feature

> The repository layer — the ONLY seam a ViewModel talks to. It wraps a `core/store` `Store` into a
> `ScreenDataStream` (the screen read contract) and exposes suspend mutators for writes. Features
> NEVER touch `Store`, DAOs, or APIs directly; they inject a repository interface.

## Call sequence

1. **Declare the repository interface** with:
   - `fun <name>Stream(scope: CoroutineScope): ScreenDataStream<T>` — the reactive read the screen binds to.
   - `fun <name>DetailStream(id: String, scope: CoroutineScope): ScreenDataStream<T>` for detail/projection screens.
   - `suspend fun upsert(...)` / `suspend fun delete(id)` for writes (offline/mutable archetypes).
   - Plain `Flow<...>` accessors for derived totals (e.g. `observeTotalMonthlyEmi()`).
   Mirror `LoanRepository` (offline) or `CryptoRepository` (network+cache, paging via `PagingScreenStream`).
2. **Implement it** (`impl/<Name>RepositoryImpl`): inject the `Store` from `core/store`
   (`Store<Key, Domain>` via its `AppStoreRegistry` qualifier) and build the read stream with
   `store.asScreenStream(scope, key)` / `store.asPagingScreenStream(...)`. Writes call
   `store.write(...)` (mutable) or the DAO's `upsert` (offline-local).
3. **Bind in `RepositoryModule`** (`single<YourRepository> { YourRepositoryImpl(get(), ...) }`).
4. **The ViewModel injects the interface** and reads `repo.<name>Stream(viewModelScope).state`
   (a `Flow<ScreenState<T>>`) — see core/ui + the feature ViewModel.

## Notes

- The `ScreenDataStream` (from `core-base/store`) carries `ScreenState.{Loading,Content,Empty,Error}`
  so the screen never hand-folds a nullable `Flow`.
- `UserDataRepository` is the reactive user/session read; `RepositoryModule` is the single DI wiring point.

Canonical example: feature/loans (`LoanRepository` offline read + write), feature/crypto (`CryptoRepository` network+cache).

Symbols: LoanRepository, CryptoRepository, WatchlistRepository, UserDataRepository, RepositoryModule
