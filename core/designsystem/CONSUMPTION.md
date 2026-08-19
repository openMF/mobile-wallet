# Consuming `core/designsystem` in a feature

> The brand token layer above `core-base/designsystem`'s Material 3 primitives — theme wrapper,
> semantic finance/spacing/elevation tokens, and the domain composables built on them
> (money text, status chips, charts). This is where a fork's visual identity lives.

## Call sequence

1. **Wrap the app in `KptTheme`** once, at the root — it builds the light/dark `ColorScheme`
   from `lightScheme`/`darkScheme`, wires `KptMaterialTheme` (from `core-base/designsystem`), and
   provides `LocalFinanceColors`, `LocalSpacing`, `LocalElevation`, `LocalMotion`, and
   `LocalScreenStateDefaults` (sourced from `core/store`'s `appScreenStateDefaults()`) so every
   screen underneath resolves branded tokens and `ScreenContent` state visuals automatically.
2. **Read tokens via the `MaterialTheme` extension properties** — `MaterialTheme.finance` for
   `FinanceColors` (`moneyPositive`/`moneyNegative`, `rateUp`/`rateDown`, `freshnessFresh`/
   `freshnessStale`/`freshnessUpdating`/`freshnessOffline`, `urgencyOverdue`/`urgencyToday`/
   `urgencyUpcoming`/`urgencyDistant`), `MaterialTheme.spacing` for the `Spacing` scale
   (`none`/`xs`/`sm`/`md`/`lg`/…), `MaterialTheme.elevation` for `Elevation` tiers.
3. **Reach for a domain composable instead of hand-rolling one** — `MoneyText`/`AmountDisplay`
   for currency (auto sign-coloring via `MoneyTone`), `StatusChip`/`RateBadge`/`UrgencyDot` for
   state-at-a-glance pills, `CardStateBox`/`CardLoadingSkeleton`/`RowLoadingShimmer`/`ErrorChip`
   for loading/error slots, and `chart/` (`KptAreaChart`, `KptBarChart`, `KptDonutChart`,
   `KptCandlestick`, `KptSparkline`) for data visualization — all read `ChartTokens` for a
   consistent multi-series palette.
4. **Icons** come from `AppIcons` (`icon/AppIcons.kt`) — add new entries there rather than
   inlining `Icons.Filled.*` per feature.

## Notes

- Override brand without forking a widget: provide your own token set above `KptTheme` —
  `CompositionLocalProvider(LocalFinanceColors provides myForkFinanceColors()) { KptTheme { App() } }`.
- Hex values in `lightFinanceColors()`/`darkFinanceColors()` are WCAG-AA contrast-verified against
  the corresponding surface tokens — keep that bar when overriding.
- Base Material 3 primitives (buttons, text fields, `KptTopAppBar`) live in `core-base/designsystem`
  — this module only adds tokens and domain composables built on top.

Canonical example: feature/home (dashboard tiles using `MoneyText` + `KptSparkline`), feature/rates
(`RateBadge` + `KptAreaChart`).

Symbols: KptTheme, FinanceColors, MaterialTheme.finance, Spacing, MaterialTheme.spacing, ChartTokens, MoneyText, StatusChip, AppIcons
