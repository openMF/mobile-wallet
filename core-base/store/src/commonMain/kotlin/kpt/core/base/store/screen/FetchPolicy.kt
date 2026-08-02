/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.screen

/**
 * Controls whether a screen stream reads from cache, hits the network, or both.
 *
 * Pass to [ScreenDataStream.asScreenStream], [LoadOnceStream.asLoadOnceStream], or
 * [PagingScreenStream] to override the default network-with-cache behaviour.
 *
 * **Choosing a policy:**
 * | Scenario                                                                    | Policy                         |
 * |-----------------------------------------------------------------------------|--------------------------------|
 * | Normal screen — show cached data immediately, refresh in background         | [NETWORK_WITH_CACHE] (default) |
 * | Always-fresh data required (e.g. payment confirmation)                      | [NETWORK_ONLY]                 |
 * | Offline-only view or explicit "load from cache"                             | [CACHE_ONLY]                   |
 * | Ambient surface that silently re-fetches on a cadence (FX rates, tickers)  | [PERIODIC]                     |
 *
 * Modelled as a sealed interface so the vocabulary is open to additional variants
 * without breaking the source-compatible `FetchPolicy.NETWORK_WITH_CACHE`-style
 * access pattern.
 */
sealed interface FetchPolicy {

    /**
     * Emit cached data immediately (if present), then trigger a background network fetch
     * and emit refreshed data when it arrives.
     *
     * This is the default and works well for most screens. The user sees content quickly
     * while the data is silently refreshed in the background. The staleness banner from
     * [DataFreshnessIndicator] is shown when the data is older than the configured TTL.
     */
    data object NETWORK_WITH_CACHE : FetchPolicy

    /**
     * Skip the local cache entirely and always fetch from the network.
     *
     * Use for screens where stale data would be misleading or harmful (e.g. payment status,
     * balance after a transaction). If the network fails the stream emits
     * [ScreenState.NoNetwork] or [ScreenState.Error] instead of showing stale data.
     */
    data object NETWORK_ONLY : FetchPolicy

    /**
     * Read only from the local cache; never perform a network request.
     *
     * Use for explicitly offline screens or when the calling code knows connectivity is
     * unavailable and wants to avoid error flicker. If the cache is empty the stream emits
     * [ScreenState.Empty].
     *
     * **DataFreshness behaviour:** the stream emits [kpt.core.base.store.screen.DataFreshness.STALE]
     * when the cached data is older than the TTL. [DataFreshness.UPDATING] is never emitted
     * in CACHE_ONLY mode, so no refreshing banner shows. Feature screens handle stale presentation
     * inside their `content { data, freshness -> }` lambda by checking `freshness == DataFreshness.STALE`.
     */
    data object CACHE_ONLY : FetchPolicy

    /**
     * Network-with-cache on the read side, plus a periodic background refresh.
     *
     * Use for ambient surfaces that should silently re-fetch on a cadence — live FX
     * rates, market tickers, dashboard tiles. The read semantic is identical to
     * [NETWORK_WITH_CACHE] (cached value emitted immediately, network refresh
     * layered in); the periodic refresh is fired by [ScreenDataStream] internally
     * via the same refresh-trigger pipeline as the reconnect refresh and the
     * user-tap refresh — so it goes through the user-tap debounce window, the
     * staleness banner, and the lastContent preservation logic for free.
     *
     * **Tuning notes:**
     * - [intervalMillis] must be positive. Values shorter than ~5s on mobile
     *   risk battery drain; values shorter than the
     *   [DEFAULT_USER_REFRESH_DEBOUNCE_MS] (1s) will be coalesced by the
     *   user-tap debounce anyway.
     * - [foregroundOnly] is wired as a flag today but the foreground-aware
     *   gating is deferred to a follow-up phase (needs `LocalAppForegroundFlow`
     *   threading through core-base/ui). The ticker fires unconditionally for
     *   now; the flag is parsed and held so callers can declare intent without
     *   waiting for the runtime gate.
     *
     * @property intervalMillis Refresh cadence in milliseconds. Must be `> 0`.
     * @property foregroundOnly If true (default), refresh only when the app is
     *   in the foreground. Currently informational — see "Tuning notes".
     */
    data class PERIODIC(
        val intervalMillis: Long,
        val foregroundOnly: Boolean = true,
    ) : FetchPolicy {
        init {
            require(intervalMillis > 0L) { "intervalMillis must be positive (got $intervalMillis)" }
        }
    }

    /**
     * Cache-first stale-while-revalidate: emit the cached value immediately
     * (via [org.mobilenativefoundation.store.store5.StoreReadRequest.cached] with
     * `refresh = false`), then consult
     * [kpt.core.base.store.freshness.FreshnessBands.bandFor] on each cache
     * emission and fire the background refresh **only** when the band is
     * [kpt.core.base.store.freshness.FreshnessBand.Stale] or
     * [kpt.core.base.store.freshness.FreshnessBand.VeryStale]. Pair with
     * [ScreenDataStream.refreshFresh] for the sibling `refresh = true`
     * fresh-fetch path (pull-to-refresh, post-mutation) — the pair together
     * satisfies Store5 S5-5 (single source of truth for freshness).
     *
     * The [ScreenDataStream] global default is still [NETWORK_WITH_CACHE]; opt
     * in per call site via `asScreenStream(fetchPolicy = CACHE_FIRST_SWR)`.
     */
    data object CACHE_FIRST_SWR : FetchPolicy
}
