# Consuming `core/firebase` in a feature

> The project-owned analytics facade over the `cmp-firebase` library's `AnalyticsHelper` —
> `core/firebase` = project layer over the `core-base/firebase` host, adding an app-specific event
> catalogue and a domain tracker rewired onto the library's interface. Ships **no-op by default**
> (privacy-respecting open-source build); a fork opts into a real provider by overriding one Koin
> binding.

## Call sequence

1. **Install the default binding** — `coreFirebaseModule` (`di/AnalyticsModule.kt`) binds
   `AnalyticsHelper` to `AnalyticsModule.Mode.NoOp`: every event call becomes a silent no-op until
   a fork overrides it.
2. **Obtain the tracker wherever `AnalyticsHelper` is injected** — `analyticsHelper.kptTracker()`
   (or `KptAnalyticsTracker(analyticsHelper)` directly) returns a `KptAnalyticsTracker` with
   domain-framed methods: `trackLogin`, `trackLoanOperation`, `trackSync`, `trackPerformance`,
   `trackOfflineOperation`, plus the wider catalogue in `KptAnalyticsTracker.kt`.
3. **From Compose**, call `rememberKptAnalyticsTracker()` instead of threading the tracker through
   parameters — it derives from `rememberAnalyticsHelper()` (from `cmp-firebase`'s compose bridge)
   and is `remember`-scoped to it.
4. **Reach for an extension function** for one-off events instead of a full tracker call —
   `AnalyticsHelper.trackApiCall`, `trackNavigation`, `trackValidationError`, `trackDataSync`,
   `trackPreferenceChange`, etc. (`KptAnalyticsExtensions.kt`).
5. **Event-name / param-key constants** live in `KptEventTypes` and `KptParamKeys`
   (`KptAnalyticsEvents.kt`) — add new ones there rather than inlining string literals.

## Fork opt-in (last-binding-wins Koin)

- **Real analytics provider** — include `core-base/firebase`'s `firebaseModule` (real Firebase via
  `AnalyticsModule.Mode.Firebase`) instead of, or after, `coreFirebaseModule`. Requires
  `google-services.json` (Android) / `GoogleService-Info.plist` (iOS).
- **Debug logging** — `single<AnalyticsHelper> { AnalyticsModule.analyticsHelper(AnalyticsModule.Mode.Stub) }`.
- **Third-party provider** (Mixpanel, Amplitude, …) — bind your own `AnalyticsHelper`
  implementation in a module installed after `coreFirebaseModule`.

## Notes

- `CrashReporter` (`core-base:observability`) is a separate seam for non-fatal errors — don't
  conflate it with analytics.
- Keep event params low-cardinality and free of sensitive data — `AnalyticsEvent` validates key
  length (≤40 chars) and value length (≤100 chars) but not content.

Canonical example: feature/loans (`trackLoanOperation` on apply/approve/repay), feature/settings
(sync/offline event tracking via `trackSync`/`trackOfflineOperation`).

Symbols: coreFirebaseModule, KptAnalyticsTracker, kptTracker, rememberKptAnalyticsTracker, KptEventTypes, KptParamKeys
