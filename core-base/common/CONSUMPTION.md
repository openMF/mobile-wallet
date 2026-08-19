# Consuming `core-base/common` in a fork

> The lowest layer in the module graph — pure-Kotlin utilities with no Compose, no Ktor, no Room,
> no feature/domain knowledge. `core/common` re-exports this module (`api(projects.coreBase
> .common)`) so features depend on `core/common`, never on this module directly.

## Call sequence

1. **Get a `DispatcherManager`** via Koin (`CommonModule` binds it per-platform through the
   `expect val dispatcherManagerModule`) instead of hardcoding `Dispatchers.IO` / `.Default` /
   `.Main` — inject it into any class needing `.default`, `.main`, `.io`, `.unconfined`, or the
   long-lived `.appScope` (used by `TimeZoneMonitorImpl`'s `shareIn`).
2. **Use the `ByteArray`/`String` Base64 extensions** (`ImageExtension.kt`) for KMP-safe encoding
   instead of a platform-specific codec: `toBase64()`, `toBase64DataUri(mimeType)`,
   `fromBase64()`/`fromBase64OrNull()`, `fromBase64DataUri()`/`fromBase64DataUriOrNull()`,
   `extractMimeTypeFromDataUri()`.
3. **Use `@Parcelize`/`Parcelable`** (`Parcelize.kt`, expect/actual) for any data class that must
   cross a platform boundary needing parcelable semantics (e.g. Android SavedStateHandle) —
   annotate the class with the expect `@Parcelize`; the actual resolves to the platform mechanism.
4. **Include `CommonModule`** (already pulled in transitively by every module that depends on
   `core/common`) rather than re-binding `DispatcherManager` yourself.

## Notes

- This module has zero Compose/Ktor/Room dependencies by design — if what you're adding needs any
  of those, it belongs in `core/common`, `core-base/network`, or `core-base/database`, not here.
- `DispatcherManagerImpl` differs only by platform for coroutine-context wiring; consumers always
  inject the `DispatcherManager` interface, never the impl.
- Framework-shared (E2/T3) — a fork extends utilities in `core/common`, not here.

Canonical example: `core-base/data`'s Android `TimeZoneMonitorImpl` (injects `DispatcherManager`
for `.io`/`.appScope`); `core/common`'s formatters, which sit alongside this module's re-export.

Symbols: DispatcherManager, CommonModule, ByteArray.toBase64, String.fromBase64, Parcelize, Parcelable
