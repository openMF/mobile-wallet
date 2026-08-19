# `core-base/observability`

Framework-shared crash / non-fatal-error reporting seam — brand-neutral, no domain logic. There is no
sibling `core/observability` wrapper; a fork binds directly to this module's `CrashReporter`.

## What's here

- **`CrashReporter`** — the seam interface: `recordException(throwable, message?)`,
  `recordMessage(message, level)`, `setUser(userId?)`, `isConfigured`. Deliberately separate from
  analytics event-tracking (the `cmp-firebase` `AnalyticsHelper`) — crash reporting is low-volume,
  stack-trace-heavy, engineering-facing, and most production stacks (Crashlytics + Firebase Analytics,
  Sentry + Mixpanel) ship them as separate SDKs with separate retention policies.
- **`ConsoleCrashReporter`** — the default stdout implementation; `isConfigured = false`. Fine for local
  development and CI, not for shipped builds.
- **`CrashSeverity`** — `Debug | Info | Warning | Error | Fatal` vocabulary for `recordMessage`.
- **`observabilityModule`** (Koin) — binds `CrashReporter` to `ConsoleCrashReporter`.

## How it fits

`core-base/store`'s `OfflineSubmitSyncer` takes an **optional** `CrashReporter?` (defaults to `null`)
so retry-loop exhaustion can be reported without a hard dependency. `observabilityModule` is **not**
auto-included by `cmp-navigation`'s `KoinModules.allModules` today — a fork adds it explicitly (or
overrides the `CrashReporter` binding directly) once it wires a real provider. See `CONSUMPTION.md`.
