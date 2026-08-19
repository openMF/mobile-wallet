# Consuming `core/platform` in a feature

> A thin re-export boundary over `core-base/platform` (`G-CORE-BASE-ENCAP`) — depend on
> `core/platform`, never on `core-base/platform` directly. It also holds any fork-owned
> `expect`/`actual` platform bridge that `core-base/platform` doesn't already provide.

## Call sequence

1. **Depend on `core/platform`, not `core-base/platform`** — `implementation(projects.core.platform)`
   in your module's `build.gradle.kts`. The `api(projects.coreBase.platform)` declaration inside
   `core/platform` re-exports `platformModule`, `GarbageCollectionManager`, and `tryCollect`
   transitively, so you get the full `core-base/platform` surface through one dependency.
2. **Install `platformModule`** in your Koin graph wherever the app wires DI — it's the framework
   platform-services module re-exported through this boundary.
3. **Adding a NEW platform bridge** (a capability `core-base/platform` doesn't cover — a
   permission requester, biometric prompt, deep-link opener, share-sheet opener, calendar-event
   creator, notification scheduler):
   - Declare the contract as an `expect` function/class in this module's `commonMain`.
   - Provide the `actual` implementation per target source set
     (`src/androidMain`, `src/iosMain`, `src/desktopMain`, `src/wasmJsMain`/`src/jsMain`,
     `src/nativeMain` as applicable).
   - Consumers keep importing from `core/platform` — the boundary doesn't change.

## Notes

- This module currently ships **zero Kotlin source** — it is purely the `build.gradle.kts`
  re-export declaration. That's by design, not a gap: it stays a pass-through until a fork or
  feature needs its own platform bridge, at which point step 3 above is where that code lands.
- Do not depend on `core-base/platform` directly from a `feature/{F}` or app-shell module — that
  bypasses the encapsulation boundary this module exists to enforce.

Canonical example: `cmp-navigation`/`cmp-shared` consuming `platformModule` through `core/platform`
for DI wiring; the bill-reminder scheduler (now `feature/bills` + the cross-platform sync worker
infra) is the historical precedent for a feature-triggered platform bridge that used to live here.

Symbols: platformModule, GarbageCollectionManager, tryCollect
