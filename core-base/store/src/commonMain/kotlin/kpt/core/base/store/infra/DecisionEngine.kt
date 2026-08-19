/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kpt.core.base.store.error.ErrorCategory
import kpt.core.base.store.error.categorize
import kpt.core.base.store.freshness.FreshnessBands
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.StoreData

/**
 * Pure function combining StoreData metadata + NetworkStatus into ScreenState.
 * No side effects, no coroutines — exhaustively unit testable.
 *
 * Uses full [NetworkStatus] (not just Boolean) to detect captive portals.
 * Error classification delegates to [categorize] — single source of truth.
 */
object DecisionEngine {

    /**
     * Maps [storeData] + [networkStatus] to the correct [ScreenState].
     *
     * @param storeData snapshot from the Store pipeline (data, error, freshness flags)
     * @param networkStatus current connectivity; [NetworkStatus.CaptivePortal] is treated
     *   as offline for content decisions but surfaces a distinct UI flag
     * @param fetchPolicy informational only — surfaced so callers can pass-through the
     *   policy that drove the upstream Store5 request shape for telemetry / debug logging.
     *   The case mapping below is **purely** a function of `(storeData, networkStatus)`;
     *   policy-specific behaviour materialises at the stream layer
     *   (see `streamDataForPolicy` in `StoreDataExtensions.kt`), not inside this decider.
     * @return the [ScreenState] variant the screen should render
     */
    fun <T> decide(
        storeData: StoreData<T>,
        networkStatus: NetworkStatus,
        @Suppress("UNUSED_PARAMETER")
        fetchPolicy: FetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
    ): ScreenState<T> {
        val noData = storeData.isEmpty
        val error = storeData.error
        val isOnline = networkStatus is NetworkStatus.Available
        val isCaptivePortal = networkStatus is NetworkStatus.CaptivePortal

        // === No data branch ===
        if (noData) {
            return when {
                isCaptivePortal -> ScreenState.NoNetwork(isCaptivePortal = true)
                !isOnline -> ScreenState.NoNetwork()
                error != null -> when (categorize(error)) {
                    ErrorCategory.Network, ErrorCategory.Timeout.Connect, ErrorCategory.Timeout.Read ->
                        ScreenState.NoNetwork()
                    ErrorCategory.Auth -> ScreenState.Unauthenticated
                    ErrorCategory.RateLimit,
                    ErrorCategory.QuotaExceeded,
                    ErrorCategory.Generic,
                    is ErrorCategory.Server,
                    is ErrorCategory.ClientError,
                    -> ScreenState.Error(error, isNetworkError = false)
                }
                else -> ScreenState.Loading
            }
        }

        // === Has data branch ===
        // Phase A of data-freshness-redesign epic (2026-06-17): network state no longer
        // conflated into DataFreshness on the Content path. Offline / captive-portal /
        // error are surfaced separately:
        //  - Network connectivity → global ConnectivityBanner (already shipped)
        //  - Per-card staleness    → FreshnessSignal sibling Flow (decideFreshness())
        // Here we just record request state: UPDATING during in-flight network refresh
        // (legitimate request signal — NOT network state), FRESH otherwise.
        val fetchedAt = storeData.fetchedAtInstant
        @Suppress("UNUSED_VARIABLE")
        val networkStateNoLongerConsumedHere = isOnline || isCaptivePortal || (error != null)
        // freshnessSignal default (Initial band) is overwritten by ScreenDataStream's
        // sibling Flow which computes the real signal via decideFreshness(). Here we
        // just bake in isRefreshing so the refreshing-banner visibility check works.
        return ScreenState.Content(
            data = storeData.data,
            fetchedAt = fetchedAt,
            freshnessSignal = FreshnessSignal.initial().copy(isRefreshing = storeData.isRefreshing),
        )
    }

    /**
     * Pure sibling function: maps [storeData] + [ttl] to a [FreshnessSignal].
     *
     * Decoupled from [decide]; runs in parallel and outputs the per-card freshness
     * signal consumed by `FreshnessIndicator`. `networkStatus` is accepted for
     * API symmetry with [decide] but **deliberately ignored** — freshness is purely
     * time-relative + last-error-aware. Network connectivity is rendered separately
     * by `ConnectivityBanner`.
     *
     * @param storeData snapshot from the Store pipeline (uses `fetchedAtInstant` + `error`)
     * @param networkStatus accepted for API symmetry; NOT read
     * @param ttl per-store TTL bound (from `AppStoreRegistry.Ttl`); above this age the
     *   band degrades from Fresh → Stale → VeryStale
     */
    @OptIn(ExperimentalTime::class)
    fun <T> decideFreshness(
        storeData: StoreData<T>,
        @Suppress("UNUSED_PARAMETER")
        networkStatus: NetworkStatus,
        ttl: Duration,
    ): FreshnessSignal {
        val now = Clock.System.now()
        val lastSyncedAt = storeData.fetchedAtInstant
        val lastError = storeData.error
        val band = FreshnessBands.bandFor(
            now = now,
            lastSyncedAt = lastSyncedAt,
            ttl = ttl,
            lastError = lastError,
        )
        return FreshnessSignal(
            lastSyncedAt = lastSyncedAt,
            ttl = ttl,
            lastError = lastError,
            band = band,
        )
    }
}
