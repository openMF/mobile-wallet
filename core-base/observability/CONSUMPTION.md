# Consuming `core-base/observability`

> Framework-shared crash-reporting seam — no `core/observability` wrapper. Unlike `core-base/network`
> or `core-base/store`, a fork binds `CrashReporter` directly at this level, and must opt in explicitly.

## Call sequence

1. **Opt in** — `observabilityModule` is defined but not included in `cmp-navigation`'s
   `KoinModules.allModules`. Add `includes(observabilityModule)` to your app's Koin module graph (or
   bind `CrashReporter` yourself) if you want it resolvable via injection.
2. **Ship a real provider** by overriding the binding in your own module — Koin's last-binding-wins
   means `observabilityModule` itself never needs to change:
   ```kotlin
   single<CrashReporter> { FirebaseCrashlyticsReporter() }
   ```
3. Inject `CrashReporter` (or pass it explicitly — see `OfflineSubmitSyncer`'s optional
   `crashReporter: CrashReporter? = null` constructor param) wherever you catch-and-continue: retry-loop
   exhaustion, recovered errors worth engineering visibility. Call
   `recordException(throwable, message)`. Uncaught exceptions stay the platform-level handler's job.
4. Call `setUser(userId)` on sign-in and `setUser(null)` on sign-out so subsequent crash reports
   attribute correctly across session boundaries.

## Notes

- `isConfigured` is `false` for the stdout default — gate a "crash reporting disabled" dev-mode banner
  on it if useful.
- `recordMessage(message, level)` is for breadcrumb-style context, not discrete events — most providers
  persist a ring buffer of these and attach it to the next crash.
- Framework-owned: the `CrashReporter` contract and `ConsoleCrashReporter` default. A fork only ever
  adds a new binding, never edits this module.

Canonical example: `core-base/store`'s `OfflineSubmitSyncer` accepts an optional `CrashReporter` for
retry-exhaustion reporting — the only shipped consumer today.

Symbols: CrashReporter, ConsoleCrashReporter, CrashSeverity, observabilityModule
