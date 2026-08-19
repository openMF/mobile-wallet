# Consuming `core/common` in a feature

> KMP-safe utilities every layer can depend on — no Compose, no Ktorfit, no Room, no DI. Three
> formatters plus a re-export of `core-base/common` (base utilities, `CommonModule` DI), so
> features depend on `core/common`, never `core-base/common` directly.

## Call sequence

1. **Format a date** — `formatDate(millis: Long): String` converts epoch millis to `dd/MM/yyyy`
   using `kotlinx.datetime`. Use for any raw timestamp a screen renders.
2. **Format a relative time** — `formatTimeAgo(instant: Instant?): String?` returns a human label
   ("just now" / "5m ago" / "2h ago" / "3d ago"), or `null` for a null instant. Use for
   freshness/last-synced copy.
3. **Format numbers without `String.format`** — `String.format` is JVM-only and fails on
   Kotlin/JS and Kotlin/Native, so this module ships KMP-safe replacements:
   - `Double.formatDecimal(places: Int): String` — fixed decimal places, no grouping.
   - `Double.formatGrouped(places: Int): String` — fixed decimal places + thousand separators.
   - `Long.formatGrouped(): String` — integer thousand separators.
4. **Everything else** (logging via `kermit.logging`, `kotlinx.datetime`, and the re-exported
   `core-base/common` utilities/DI) flows through the `api(projects.coreBase.common)` dependency —
   import it from `core/common`, not `core-base/common`.

## Notes

- These are pure-Kotlin, platform-agnostic helpers — no locale-aware `NumberFormat`/`DateFormat`.
  If a feature needs locale-sensitive formatting, that's a feature-local concern, not this module's.
- Currency symbol/sign presentation (color, `+`/`-` prefix) is `core/designsystem`'s job
  (`MoneyText`) — `core/common` only produces the numeric string.

Canonical example: feature/loans, feature/bills (due-date + amount formatting), feature/rates
(rate-value formatting).

Symbols: formatDate, formatTimeAgo, Double.formatDecimal, Double.formatGrouped, Long.formatGrouped
