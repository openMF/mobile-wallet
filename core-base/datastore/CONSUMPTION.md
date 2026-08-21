# Consuming `core-base/datastore` in a fork

> The framework-shared key-value substrate — two Koin-qualified `Settings` instances (plain +
> encrypted) plus the sync-bookkeeping persistence primitive. `core/datastore`'s
> `UserPreferencesRepository` is the ONLY consumer that should read `Settings` directly; features
> go through that repository, never through this module.

## Call sequence

1. **Include `DatastoreBaseModule`** (already pulled in by `core/datastore`'s `DatastoreModule`) to
   get `single<Settings>(named("plain"))` and `single<Settings>(named("secure"))` bound.
2. **Inject the right `Settings` qualifier** when adding a new preference group in
   `core/datastore`'s `UserPreferencesRepositoryImpl` — `get<Settings>(named("plain"))` for
   ordinary prefs, `get<Settings>(named("secure"))` for tokens/passcodes. Never instantiate
   `SecureSettingsFactory` yourself; it's wired once by `DatastoreBaseModule`.
3. **Reuse `SyncStatePersister`** for anything sync-related instead of adding a parallel
   persistence path — `synchronizer.getChangeListVersions()` / `updateChangeListVersions { ... }`
   (from `core-base/data`) delegate to `SettingsSyncStatePersister`, which reads/writes
   `ChangeListVersions` through the `plain` `Settings` instance under one fixed key.
4. **To swap the `secure` backing store** (e.g. a fork wants a different Keychain service name or
   an HSM-backed Android implementation), override the platform `SecureSettingsFactory` actual —
   the `Settings` interface consumers depend on doesn't change.

## Notes

- `secure` is not "more persistent" than `plain` — both survive process restart, not data wipe;
  `secure` only adds platform encryption-at-rest.
- Don't reach for this module directly from a feature — go through `core/datastore`'s
  `UserPreferencesRepository` so all prefs stay discoverable in one place (see
  `core/datastore/CONSUMPTION.md`).
- Framework-shared (E2/T3) — don't add fork-specific preference keys here; add them to
  `UserPreferencesRepository` in `core/datastore`.

Canonical example: `core/datastore`'s `DatastoreModule` (includes `DatastoreBaseModule`,
binds `SyncStatePersister`), `sync/`'s `DataSyncWorker` (reads/writes via `Synchronizer`).

Symbols: DatastoreBaseModule, SecureSettingsFactory, ChangeListVersions, SyncStatePersister, SettingsSyncStatePersister
