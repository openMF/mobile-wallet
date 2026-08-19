# Consuming `core/datastore` in a feature

> Key-value preferences (NOT Store5, NOT Room-entity data). Backed by `multiplatform-settings`
> (`plain` + `secure` variants). This is the home for user settings / session flags / onboarding
> state — anything that is a small typed preference, never a cached network entity.

## Call sequence

1. **Read reactively** by injecting `UserPreferencesRepository` and observing its `Flow`/`StateFlow`
   accessors (`userData: StateFlow<UserData>`, `observeLanguage`, `observeDarkThemeConfig`,
   `observeDynamicColorPreference`, `observeScreenCapturePreference`).
2. **Write** via the suspend setters (`setLanguage`, `setDarkThemeConfig`, `setIsAuthenticated`,
   `setShowOnboarding`, `setFirstTimeState`, …) — each persists to the underlying `Settings`.
3. **For a NEW preference group**, add methods to `UserPreferencesRepository` (+ `Impl`), backed by
   `get<Settings>(named("plain"))` for ordinary prefs or `named("secure")` for tokens/passcodes.
   All wiring is one `single { }` in `DatastoreModule`.
4. **The ViewModel injects the repository** and maps its flows into its `ScreenState` /
   settings UI — no direct `Settings` access from a feature.

## Notes

- Use `secure` for anything sensitive (`authToken`, `passcode`); `plain` for UI/UX prefs.
- `SyncStatePersister` (`SettingsSyncStatePersister`) tracks change-list versions for sync features —
  reuse it, don't add a parallel persistence path.
- For cached network data or offline records, use `core/database` + `core/store`, NOT datastore.

Canonical example: feature/settings (theme/language/security prefs), feature/profile (session flags).

Symbols: UserPreferencesRepository, UserPreferencesRepositoryImpl, DatastoreModule, SyncStatePersister, SettingsSyncStatePersister, ChangeListVersions
