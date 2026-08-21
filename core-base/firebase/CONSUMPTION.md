# Consuming `core-base/firebase` in a fork

> The template-owned host binding for the published `cmp-firebase` library — the "analytics ON"
> module. `core/firebase` (project layer) binds the NoOp default and the domain tracker; this
> module is what a fork includes to turn real Firebase Analytics + Performance Monitoring on.

## Call sequence

1. **Include `firebaseModule`** in your app's Koin setup instead of, or after,
   `core/firebase`'s `coreFirebaseModule` — Koin's last-binding-wins resolution means whichever
   module is installed later wins the `single<AnalyticsHelper>` binding.
   ```kotlin
   startKoin { modules(coreFirebaseModule, firebaseModule, /* ... */) } // firebaseModule wins
   ```
2. **Provide platform Firebase config** — `google-services.json` (Android) /
   `GoogleService-Info.plist` (iOS) must be present; `AnalyticsModule.Mode.Firebase` falls back to
   the Measurement-Protocol/NoOp path on platforms without a native SDK.
3. **Consume through `core/firebase`, not directly** — inject `AnalyticsHelper` and call
   `analyticsHelper.kptTracker()` (or `rememberKptAnalyticsTracker()` in Compose) as documented in
   `core/firebase/CONSUMPTION.md`. This module only changes which implementation is bound; the
   domain-tracker API surface is unchanged.
4. **Performance Monitoring** — inject the bound `PerformanceTracker` (from
   `AnalyticsModule.performanceTracker(get())`) wherever you want to trace a custom operation;
   it's separate from `CrashReporter` (`core-base/observability`), which handles non-fatal errors.

## Notes

- This module is framework-shared (E2/T3, G-CORE-BASE-ENCAP) — it only re-exports + binds the
  library; it never carries app-specific event names. Add those to `core/firebase`'s
  `KptEventTypes` / `KptParamKeys` instead.
- `api(libs.cmp.firebase)` / `api(libs.cmp.firebase.compose)` are deliberately `api`, not
  `implementation`, so `core/firebase` and the app shell see the library's types through
  `core-base/firebase` without a direct dependency of their own.
- Don't add a Compose plugin here — Compose-facing helpers come from `cmp-firebase-compose`
  transitively; consumers that call them apply the Compose plugin themselves.

Canonical example: `core/firebase`'s `README.md` §"Fork-customisation seam" (the 4-option table:
NoOp / console stub / this module's real Firebase / a custom provider).

Symbols: firebaseModule, AnalyticsModule, AnalyticsConfig, AnalyticsHelper, PerformanceTracker
