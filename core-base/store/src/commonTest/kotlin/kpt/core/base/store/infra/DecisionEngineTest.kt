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

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkInfo
import kpt.core.base.store.freshness.FreshnessBand
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.DataOrigin
import kpt.core.base.store.screen.StoreData
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

class DecisionEngineTest {

    private val available = NetworkStatus.Available(
        NetworkInfo(type = NetworkType.WiFi, isMetered = false),
    )
    private val unavailable = NetworkStatus.Unavailable
    private val captivePortal = NetworkStatus.CaptivePortal(
        NetworkInfo(type = NetworkType.WiFi, isMetered = false),
    )

    // === No data branch ===

    @Test
    fun `no data + Available + no error = Loading`() {
        val result = DecisionEngine.decide(emptyStoreData(), available)
        assertIs<ScreenState.Loading>(result)
    }

    @Test
    fun `no data + Unavailable + no error = NoNetwork`() {
        val result = DecisionEngine.decide(emptyStoreData(), unavailable)
        assertIs<ScreenState.NoNetwork>(result)
        assertEquals(false, result.isCaptivePortal)
    }

    @Test
    fun `starvation-fix integration - empty sentinel while refreshing + Unavailable = NoNetwork`() {
        // Integration contract for the StoreDataMapper starvation fix:
        // mapToStoreDataNoFallback now emits StoreData(EMPTY_SENTINEL, isEmpty=true,
        // isRefreshing=true) on Initial/Loading-without-cache. DecisionEngine must route
        // this to NoNetwork when offline, preventing the "spinner stuck ~30s waiting for
        // HTTP timeout" UX bug on cold-start offline launches (Coin Detail offline).
        val starvationFixEmission = StoreData(
            data = "<empty>",
            origin = DataOrigin.NETWORK,
            isEmpty = true,
            isRefreshing = true, // The new flag combination this test guards.
            error = null,
        )
        val result = DecisionEngine.decide(starvationFixEmission, unavailable)
        assertIs<ScreenState.NoNetwork>(result)
        assertEquals(false, result.isCaptivePortal)
    }

    @Test
    fun `no data + CaptivePortal + no error = NoNetwork captive=true`() {
        val result = DecisionEngine.decide(emptyStoreData(), captivePortal)
        assertIs<ScreenState.NoNetwork>(result)
        assertTrue(result.isCaptivePortal)
    }

    @Test
    fun `no data + Available + IOException = NoNetwork`() {
        val data = emptyStoreData(error = FakeIOException("timeout"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `no data + Unavailable + IOException = NoNetwork`() {
        val data = emptyStoreData(error = FakeIOException("no route"))
        val result = DecisionEngine.decide(data, unavailable)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `no data + Available + other error = Error`() {
        val error = IllegalStateException("parse error")
        val data = emptyStoreData(error = error)
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
        assertEquals(error, result.error)
        assertEquals(false, result.isNetworkError)
    }

    // === Has data branch ===

    @Test
    fun `has data + Available + no error + not refreshing = Content FRESH`() {
        val data = dataStoreData("hello")
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Content<String>>(result)
        assertEquals("hello", result.data)
        assertFalse(result.freshnessSignal.isRefreshing, "Expected not refreshing")
    }

    // Phase A of data-freshness-redesign epic (2026-06-17): DecisionEngine no longer
    // conflates network state into DataFreshness on the Content path. Network
    // connectivity → ConnectivityBanner; per-card staleness → FreshnessSignal sibling.
    // DecisionEngine.decide() Content path only encodes request-state (UPDATING during
    // in-flight refresh) — never network state, never error.

    @Test
    fun `has data + Unavailable = Content FRESH (network state in ConnectivityBanner, not DataFreshness)`() {
        val data = dataStoreData("cached")
        val result = DecisionEngine.decide(data, unavailable)
        assertIs<ScreenState.Content<String>>(result)
        assertFalse(result.freshnessSignal.isRefreshing, "Expected not refreshing")
    }

    @Test
    fun `has data + CaptivePortal = Content FRESH (network state in ConnectivityBanner, not DataFreshness)`() {
        val data = dataStoreData("cached")
        val result = DecisionEngine.decide(data, captivePortal)
        assertIs<ScreenState.Content<String>>(result)
        assertFalse(result.freshnessSignal.isRefreshing, "Expected not refreshing")
    }

    @Test
    fun `has data + refreshing = Content UPDATING (request-state, legitimate)`() {
        val data = dataStoreData("old", isRefreshing = true)
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Content<String>>(result)
        assertTrue(result.freshnessSignal.isRefreshing, "Expected refreshing")
    }

    @Test
    fun `has data + error refresh failed = Content with FreshnessSignal lastError`() {
        val data = dataStoreData("stale", error = FakeIOException("refresh failed"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Content<String>>(result)
        assertFalse(result.freshnessSignal.isRefreshing, "Expected not refreshing")
    }

    @Test
    fun `has data + refreshing + Unavailable = Content UPDATING (refreshing wins - network state ignored)`() {
        val data = dataStoreData("old", isRefreshing = true)
        val result = DecisionEngine.decide(data, unavailable)
        assertIs<ScreenState.Content<String>>(result)
        assertTrue(result.freshnessSignal.isRefreshing, "Expected refreshing")
    }

    // === error categorization (delegates to categorize()) ===

    @Test
    fun `network error - IOException direct = NoNetwork`() {
        val data = emptyStoreData(error = FakeIOException("connection reset"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `network error - IOException as cause = NoNetwork`() {
        val data = emptyStoreData(error = RuntimeException("wrapped", FakeIOException("cause")))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `generic error - non-network exception = Error`() {
        val data = emptyStoreData(error = IllegalArgumentException("bad input"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
    }


    @Test
    fun `auth error - 401 message = Unauthenticated`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 401 Unauthorized"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Unauthenticated>(result)
    }

    @Test
    fun `auth error - 403 message = Unauthenticated`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 403 Forbidden"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Unauthenticated>(result)
    }

    @Test
    fun `rate limit error - 429 message = Error`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 429 Too Many Requests"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
    }

    @Test
    fun `server error - 500 message = Error`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 500 Internal Server Error"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
    }

    // === decideFreshness() sibling — pure time-based staleness ===

    @OptIn(ExperimentalTime::class)
    @Test
    fun `decideFreshness - has data + online + fresh = Fresh band`() {
        val now = Clock.System.now()
        val data = dataStoreData("cached", fetchedAtInstant = now - 2.minutes)
        val signal = DecisionEngine.decideFreshness(data, available, ttl = 5.minutes)
        assertEquals(FreshnessBand.Fresh, signal.band)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `decideFreshness - has data + age between ttl and 3xttl = Stale band`() {
        val now = Clock.System.now()
        val data = dataStoreData("cached", fetchedAtInstant = now - 8.minutes)
        val signal = DecisionEngine.decideFreshness(data, available, ttl = 5.minutes)
        assertEquals(FreshnessBand.Stale, signal.band)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `decideFreshness - has data + offline + fresh = Fresh band (network ignored)`() {
        // freshness is pure time + error; network connectivity is NEVER read.
        val now = Clock.System.now()
        val data = dataStoreData("cached", fetchedAtInstant = now - 2.minutes)
        val signal = DecisionEngine.decideFreshness(data, unavailable, ttl = 5.minutes)
        assertEquals(FreshnessBand.Fresh, signal.band)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `decideFreshness - has data + last error = VeryStale band with lastError preserved`() {
        val now = Clock.System.now()
        val err = RuntimeException("HTTP 503")
        val data = dataStoreData("cached", fetchedAtInstant = now - 1.minutes, error = err)
        val signal = DecisionEngine.decideFreshness(data, available, ttl = 5.minutes)
        assertEquals(FreshnessBand.VeryStale, signal.band)
        assertNotNull(signal.lastError)
        assertEquals(err, signal.lastError)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `decideFreshness - no data + no error = Initial band`() {
        val data = emptyStoreData()
        val signal = DecisionEngine.decideFreshness(data, available, ttl = 5.minutes)
        assertEquals(FreshnessBand.Initial, signal.band)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `decideFreshness - has data + age over 3xttl = VeryStale band`() {
        val now = Clock.System.now()
        val data = dataStoreData("cached", fetchedAtInstant = now - 20.minutes)
        val signal = DecisionEngine.decideFreshness(data, available, ttl = 5.minutes)
        assertEquals(FreshnessBand.VeryStale, signal.band)
    }

    // === Helpers ===

    private fun emptyStoreData(error: Throwable? = null): StoreData<String> = StoreData(
        data = "", // Sentinel — isEmpty=true means data is never used by DecisionEngine
        origin = DataOrigin.NETWORK,
        isEmpty = true,
        error = error,
    )

    @OptIn(ExperimentalTime::class)
    private fun dataStoreData(
        value: String,
        isRefreshing: Boolean = false,
        error: Throwable? = null,
        fetchedAtInstant: kotlin.time.Instant? = null,
    ): StoreData<String> = StoreData(
        data = value,
        origin = DataOrigin.CACHE,
        isEmpty = false,
        isRefreshing = isRefreshing,
        error = error,
        fetchedAtInstant = fetchedAtInstant,
    )
}

/** Fake IOException for multiplatform tests (class name contains "IOException"). */
private class FakeIOException(message: String) : Exception(message)
