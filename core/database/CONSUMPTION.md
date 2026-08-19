# Consuming `core/database` in a feature

> The Room (KMP) persistence layer — the Store5 `SourceOfTruth` for every cached/offline read.
> Owns `AppDatabase`, the per-domain `@Entity` + `@Dao` pairs, and `DatabaseModule` (DAO singletons).
> Consumed by `core/store` (as the SoT) and directly by `core/data` for offline-local writes.

## Call sequence

1. **Add an `@Entity`** for the row you persist (e.g. `LoanEntity`, `tableName = "banking_loans"`).
   Keep it a storage shape distinct from the domain model (`core/model`); the entity↔domain map
   lives in the store/data layer (`LoanEntity.toDomain()` / `Loan.toEntity()`).
2. **Add a `@Dao`** with reactive reads (`fun observeAll(): Flow<List<Entity>>`,
   `observeById(id): Flow<Entity?>`), suspend writes (`@Upsert suspend fun upsert(...)`), and
   `deleteById` / `deleteAll` — mirror `LoanDao`.
3. **Register both** on `AppDatabase` (`@Database(entities = [...])` + `abstract val yourDao: YourDao`)
   and expose the DAO singleton in `DatabaseModule` (`single { get<AppDatabase>().yourDao }`).
4. **Wrap DAO reads in `daoFlow(TABLE) { dao.observeAll() }`** (from `core-base/database`) inside the
   store's `SourceOfTruth.reader` so wasmJs collectors re-emit after writes despite Room 3 alpha's
   async InvalidationTracker (no-op on Android/Desktop/iOS).

## Notes

- Infra entities/DAOs (`DraftEntity`/`DraftDao`, `BookkeeperEntity`/`BookkeeperDao`,
  `FetchedAtEntity`/`FetchedAtDao`) back the MUTABLE archetype's offline-write bookkeeping + TTL — reuse them, don't re-invent.
- `platformModule` (expect/actual) provides the `AppDatabase` builder per platform.

Canonical example: feature/loans (`LoanDao` + `LoanEntity`, offline-local), feature/currency-rates (`RateHistoryDao`, cached).

Symbols: AppDatabase, DatabaseModule, LoanDao, LoanEntity, RateHistoryDao, DraftDao, BookkeeperDao, FetchedAtDao
