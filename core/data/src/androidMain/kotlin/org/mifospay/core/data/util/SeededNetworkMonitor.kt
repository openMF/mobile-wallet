/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.util

import android.content.Context
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkChangeEvent
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkInfo
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kpt.core.data.infra.NetworkMonitor as KptNetworkMonitor

/**
 * Fixes the kmptoolkit `cmp-network-monitor` v3.5.3 **initial-seed bug**.
 *
 * `NetworkMonitorProvider.install()` on Android reports **offline forever** when the
 * device is already connected at app launch: no `onAvailable` callback fires for the
 * already-connected network, and the monitor's `isOnline`/`networkStatus` StateFlows
 * never leave their initial `false`/`Unavailable` seed. Because every Store5 read gates
 * on `networkStatus` (via `asScreenStream` → DecisionEngine), the whole offline-first
 * layer shows `NoNetwork` on-device even with validated connectivity (confirmed: a Wi-Fi
 * toggle forces `onAvailable` and clears it).
 *
 * This adapter presents the kmptoolkit [KptNetworkMonitor] contract backed by the fork's
 * [ConnectivityManagerNetworkMonitor], which correctly **seeds the current state
 * synchronously** (`channel.trySend(connectivityManager.isCurrentlyConnected())`) at
 * subscription and then tracks `onAvailable`/`onLost` — satisfying contract invariant #2
 * ("initial value MUST reflect the platform's current state").
 *
 * Bound in `AndroidDataModule` (the Android `platformModule` actual), which is
 * `includes()`d after the template's `single<NetworkMonitor> { install() }` binding, so
 * this definition wins on Android. Other platforms keep `install()` unchanged.
 *
 * NOTE (template-derived root cause): the real bug is in the shared `cmp-network-monitor`
 * dependency's Android initial-seed. This fork-local adapter is the pragmatic fix; the
 * upstream remedy is a `cmp-network-monitor` patch/upgrade honoring invariant #2.
 */
internal class SeededNetworkMonitor(
    context: Context,
    ioDispatcher: CoroutineDispatcher,
    scope: CoroutineScope,
) : KptNetworkMonitor {

    private val delegate = ConnectivityManagerNetworkMonitor(context, ioDispatcher)

    override val isOnline: StateFlow<Boolean> =
        delegate.isOnline.stateIn(scope, SharingStarted.Eagerly, true)

    override val networkStatus: StateFlow<NetworkStatus> =
        delegate.isOnline
            .map { online -> if (online) ONLINE else NetworkStatus.Unavailable }
            .stateIn(scope, SharingStarted.Eagerly, ONLINE)

    // The Store consumes only `networkStatus` (and its debounced derivation); the raw
    // change-event stream is unused, so an empty SharedFlow satisfies the contract.
    override val networkChanges: SharedFlow<NetworkChangeEvent> = MutableSharedFlow()

    override fun close() = Unit

    private companion object {
        // DecisionEngine only type-checks `is NetworkStatus.Available` — a synthetic
        // NetworkInfo suffices; connection-type/bandwidth detail is not consumed here.
        val ONLINE: NetworkStatus = NetworkStatus.Available(
            NetworkInfo(NetworkType.Unknown, false, 0, 0),
        )
    }
}
