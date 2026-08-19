# Consuming `core-base/database` in a fork

> The Room (KMP) platform layer: the `platformDatabaseModule<T>` builder, per-fork
> `DatabaseNaming`, the framework-owned infra DAOs (bookkeeping/drafts/freshness), and the Room
> invalidation bridge. `core/database` (project layer) owns `AppDatabase` and every domain
> `@Entity`/`@Dao` pair; this module owns everything that would otherwise be per-platform
> boilerplate a fork would have to re-author.

## Call sequence

1. **Build your `AppDatabase` singleton via `platformDatabaseModule<T>`**, not by hand — each
   platform's `core/database` actual is a one-line delegation:
   `actual val platformModule: Module = platformDatabaseModule<AppDatabase>(appDatabaseNaming)`.
   It owns the SQLite driver, query dispatcher, and destructive-migration fallback per platform
   (Android/Desktop/Native/JS/wasmJs actuals all live here).
2. **Pass a `DatabaseNaming`** built from the `syncForkConfig`-generated `DatabaseConfig`
   (`DatabaseNaming(fileName = DatabaseConfig.NAME, desktopDirName = DatabaseConfig.DESKTOP_DIR_NAME)`)
   so the on-disk file and desktop app-data directory are unique per fork — never hardcode a
   database file name in `core/database`.
3. **Reuse the framework-owned infra DAOs** instead of inventing parallel bookkeeping tables:
   `BookkeeperDao` backs `org.mobilenativefoundation.store.store5.Bookkeeper` (write-failure
   tracking), `FetchedAtDao` backs `FetchedAtRepository` (per-store-key freshness timestamps), and
   `DraftDao` backs the offline-resilient `SubmitHandler`/`DraftSubmitHandler` write path
   (`framework_submit_drafts`). All three are registered once in `core/database`'s `DatabaseModule`
   (`single { get<AppDatabase>().bookkeeperDao }`, etc.) — bind, don't duplicate.
4. **Wrap every DAO write/read with the invalidation bridge** — `notifyingWrite("table") { dao
   .upsert(...) }` and `daoFlow("table") { dao.observeXxx() }` — so wasmJs `Flow` consumers
   re-emit after writes despite Room 3 alpha's async `InvalidationTracker` gap. See
   `invalidation/README.md` for the full "why" and the exact 1-line-edit recipe; it's a no-op cost
   on Android/Desktop/iOS.

## Notes

- Never call `Room.databaseBuilder()` / the platform driver directly from `core/database` — that's
  exactly the boilerplate `platformDatabaseModule<T>` centralizes.
- The `commonMain` `Room.kt` expect declarations (17 annotations) + `nonJsCommonMain` actuals exist
  so `@Entity`/`@Dao`/etc. resolve identically on every non-JS target; you don't interact with them
  directly, they're what makes `@Entity`/`@Dao` compile at all in `core/database`.
- Framework-shared (E2/T3) — a fork's entities, DAOs, and `AppDatabase` registration live in
  `core/database` (see its `CONSUMPTION.md`), never here.

Canonical example: `core/database`'s `DatabaseModule.kt` (the `platformModule` delegation +
infra DAO bindings); `core/data/banking/` repositories (the `notifyingWrite`/`daoFlow` pattern).

Symbols: platformDatabaseModule, DatabaseNaming, BookkeeperDao, FetchedAtDao, DraftDao, RoomChangeBus, daoFlow, notifyingWrite
