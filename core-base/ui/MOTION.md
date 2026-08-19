# Motion — core-base/ui

Centralized documentation for navigation transitions and motion primitives in the
template. All transitions are theme-aware (consume `MaterialTheme.motion` tokens)
and obey duration-symmetry invariants enforced via `SharedAxisSpec` / `TransitionPushSpec`.

## The four Motion patterns

| Pattern | When to use | Factory |
|---|---|---|
| **Shared-axis-X** (forward/back) | Stack push and pop | `KptSharedAxis.enterForward/exitForward/enterBack/exitBack` |
| **Fade-through** (siblings) | Bottom-nav tab switches; sibling routes with no stack relationship | `KptFadeThrough.enter/exit` |
| **Push left/right** (Material 3 push) | Modal-style push within the same surface | `TransitionProviders.Enter.pushLeft/pushRight` + `Exit.pushLeft/pushRight` |
| **Slide up/down** | Bottom-sheet style entry | `TransitionProviders.Enter.slideUp/slideDown` |

## Duration-symmetry invariant

For any push/pop transition, **enter and exit phases MUST use identical durations and
delays**. Asymmetric durations cause a visible "blink" — the older screen vanishes
before the new one finishes appearing. This invariant is enforced by:

- `SharedAxisSpecTest` — asserts `SharedAxisSpec.isSymmetric(Motion()) == true`
- `TransitionPushSpecTest` — asserts `TransitionPushSpec.isSymmetric() == true`

When extending the framework with a new transition pair, add a spec helper + test
following the `SharedAxisSpec` / `TransitionPushSpec` pattern.

**Fade-through is intentionally asymmetric** — the M3 fade-through spec sequences
exit fade-out fully BEFORE enter fade-in (no overlap of competing UIs). Parity
checks DO NOT apply to fade-through; the staggered timing is by design.

## Motion tokens

`MaterialTheme.motion` (lives in `core-base/designsystem/theme/Motion.kt`) defines:

| Token | Default | Use |
|---|---|---|
| `durationShort1` | 50ms | Instant micro-interaction (chip press) |
| `durationShort2` | 100ms | Fast micro-interaction |
| `durationShort3` | 150ms | Fast feedback (ripple, hover) |
| `durationShort4` | 200ms | Fast container-state change |
| `durationMedium1` | 250ms | Standard fade / cross-fade |
| `durationMedium2` | 300ms | Standard container morph; fade-through enter |
| `durationMedium3` | 350ms | Standard slide |
| `durationMedium4` | 400ms | Slow container morph |
| `durationLong1` | 450ms | **Full-screen nav (push forward) — default for shared-axis** |
| `durationLong2` | 500ms | Full-screen nav |
| `durationLong3` | 550ms | Emphasized full-screen |
| `durationLong4` | 600ms | Extra-long full-screen |

Forks override `Motion()` once (via `KptTheme`); every transition surface picks
up the new values automatically.

## Verifying transitions in your fork

1. Install a debug build: `./gradlew :cmp-android:installDemoDebug`
2. Open the app → navigate to **Settings**
3. Long-press the **Money Toolkit** label at the bottom of the Settings screen — this opens the dev-only **Transition Gallery** (only available on non-release builds, gated via `template.core.base.security.isReleaseBuild()`)
4. Tap each transition variant; the destination auto-pops after 2s
5. Visual check: every transition should be smooth, no blink

If you see a blink, the most likely cause is a duration-symmetry regression. Run:

```bash
./gradlew :core-base:ui:desktopTest --tests "template.core.base.ui.motion.SharedAxisSpecTest" \
                                    --tests "template.core.base.ui.util.TransitionPushSpecTest"
```

A failing assertion will point to the asymmetric pair.

## Related files

| File | Purpose |
|---|---|
| `motion/KptSharedAxis.kt` | Shared-axis-X enter/exit factories |
| `motion/KptFadeThrough.kt` | Fade-through enter/exit factories (intentionally staggered) |
| `motion/SharedAxisSpec.kt` | Pure-function duration helper for shared-axis (testable) |
| `util/Transition.kt` | Flat `Enter`/`Exit` namespace for push/slide variants |
| `util/TransitionPushSpec.kt` | Pure-function timing helper for push transitions (testable) |
| `nav/NavGraphBuilderExtensions.kt` | `composableWithPushTransitions<T>()`, `composableWithSharedAxisTransitions<T>()`, etc. |

## History

The duration-symmetry invariant was introduced in Phase 08 of the
`core-base-store-coverage` epic (2026-05-27) after the audit found
`KptSharedAxis.exitForward` was using `motion.durationShort4` (200ms) for fadeOut
while `enterForward` used `motion.durationLong1` (450ms) — the 250ms gap was the
visible nav-blink. Same asymmetry existed in `TransitionProviders.Exit.pushLeft/Right`
where the slide had a `~50ms` delay (`TOTAL_DURATION_MS / 7`) that enter didn't have.
Both were fixed by extracting `SharedAxisSpec` + `TransitionPushSpec` and wiring
production code through them.
