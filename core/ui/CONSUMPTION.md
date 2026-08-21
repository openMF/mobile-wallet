# Consuming `core/ui` in a feature

> App-level reusable Compose primitives every `feature/{F}` composes — the shell (`KptScaffold`),
> navigation chrome (`KptBottomBar`, `NavigationItem`), and cross-feature input widgets. These are
> brand-neutral structural pieces (design TOKENS live in `core/designsystem`; state rendering
> primitives like `ScreenContent` live in `core-base/ui`).

## Call sequence

1. **Wrap each screen in `KptScaffold`** — pass `title`, `onNavigateBack`, `actions`, `snackbarHost`,
   and the screen body as `content`. It gives every screen a consistent top bar + insets + snackbar host.
2. **Bind the screen state → render** with `ScreenContent<T>(state) { data -> ... }` (from
   `core-base/ui`), which resolves `ScreenState.{Loading,Empty,Error,Content}` to the right slot; the
   feature only writes the Content lambda.
3. **Declare bottom-nav entries** as `NavigationItem`s (`selectedIcon`, `icon`, `labelRes`,
   `graphRoute`, `startDestinationRoute`, `testTag`) and feed them to `KptBottomBar` /
   `KptNavigationRail` in the app shell.
4. **Reuse the input widgets** where applicable — `PasswordStrengthIndicator`, `RevealSwipe`
   (swipe-to-reveal actions), `KptPullToRefreshState` (pull-to-refresh holder) — instead of
   re-authoring per feature.

## Notes

- A feature-specific composite that isn't reused elsewhere stays IN `feature/{F}`; promote to
  `core/ui` only when a second feature needs it.
- Colours / typography / shape come from `core/designsystem` tokens — `core/ui` primitives consume
  the ambient theme, they don't hardcode brand.

Canonical example: feature/home (`KptScaffold` + `KptBottomBar` shell), feature/settings (`PasswordStrengthIndicator`, `RevealSwipe`).

Symbols: KptScaffold, KptBottomBar, KptNavigationRail, NavigationItem, KptPullToRefreshState, PasswordStrengthIndicator, RevealSwipe
