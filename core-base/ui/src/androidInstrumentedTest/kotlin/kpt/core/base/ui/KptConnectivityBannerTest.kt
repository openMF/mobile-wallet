/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkChangeEvent
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkInfo
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for [KptConnectivityBanner] — the root-level offline indicator placed in
 * [cmp.navigation.ui.KptRootScaffold]'s `utilityBar` slot. Covers:
 *
 *  - Static states: banner hidden when online, visible when offline.
 *  - Dynamic transitions: banner slides in on disconnect, slides out on reconnect.
 *  - Overrides: custom message, `showBanner = false` suppresses even when offline.
 */
@RunWith(AndroidJUnit4::class)
class KptConnectivityBannerTest {

    @get:Rule
    val rule = createComposeRule()

    // ── Static state ─────────────────────────────────────────────────────────

    @Test
    fun bannerNotVisible_whenOnline() {
        rule.setContent {
            KptConnectivityBanner(
                monitor = FakeConnectivityMonitor(online = true),
                debounceMs = 0L,
            )
        }
        rule.onNodeWithText("No internet connection").assertDoesNotExist()
    }

    @Test
    fun bannerVisible_whenOffline() {
        rule.setContent {
            KptConnectivityBanner(
                monitor = FakeConnectivityMonitor(online = false),
                debounceMs = 0L,
            )
        }
        rule.onNodeWithText("No internet connection").assertIsDisplayed()
    }

    @Test
    fun bannerShowsCustomMessage_whenOffline() {
        rule.setContent {
            KptConnectivityBanner(
                monitor = FakeConnectivityMonitor(online = false),
                message = "You're offline",
                debounceMs = 0L,
            )
        }
        rule.onNodeWithText("You're offline").assertIsDisplayed()
    }

    @Test
    fun bannerNotVisible_whenShowBannerFalse_evenIfOffline() {
        rule.setContent {
            KptConnectivityBanner(
                showBanner = false,
                monitor = FakeConnectivityMonitor(online = false),
                debounceMs = 0L,
            )
        }
        rule.onNodeWithText("No internet connection").assertDoesNotExist()
    }

    // ── Dynamic transitions ───────────────────────────────────────────────────

    @Test
    fun bannerAppearsWhenNetworkDrops() {
        val monitor = FakeConnectivityMonitor(online = true)
        rule.setContent {
            KptConnectivityBanner(monitor = monitor, debounceMs = 0L)
        }

        rule.onNodeWithText("No internet connection").assertDoesNotExist()

        rule.runOnIdle { monitor.setOnline(false) }
        rule.waitForIdle()

        rule.onNodeWithText("No internet connection").assertIsDisplayed()
    }

    @Test
    fun bannerDisappearsWhenNetworkReturns() {
        val monitor = FakeConnectivityMonitor(online = false)
        rule.setContent {
            KptConnectivityBanner(monitor = monitor, debounceMs = 0L)
        }

        rule.onNodeWithText("No internet connection").assertIsDisplayed()

        rule.runOnIdle { monitor.setOnline(true) }
        rule.waitForIdle()

        rule.onNodeWithText("No internet connection").assertDoesNotExist()
    }
}

/**
 * Minimal [NetworkMonitor] test double. Only [isOnline] is reactive — the banner's
 * [io.github.mobilebytelabs.kmptoolkit.networkmonitor.compose.ConnectivityBanner]
 * collects it directly via `collectIsOnlineAsState`.
 */
private class FakeConnectivityMonitor(online: Boolean) : NetworkMonitor {

    private val _isOnline = MutableStateFlow(online)
    private val _networkStatus = MutableStateFlow<NetworkStatus>(
        if (online) NetworkStatus.Available(NetworkInfo()) else NetworkStatus.Unavailable,
    )

    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    override val networkStatus: StateFlow<NetworkStatus> = _networkStatus.asStateFlow()
    override val networkChanges: SharedFlow<NetworkChangeEvent> =
        MutableSharedFlow<NetworkChangeEvent>().asSharedFlow()

    fun setOnline(online: Boolean) {
        _isOnline.value = online
        _networkStatus.value =
            if (online) NetworkStatus.Available(NetworkInfo()) else NetworkStatus.Unavailable
    }

    override fun close() = Unit
}
