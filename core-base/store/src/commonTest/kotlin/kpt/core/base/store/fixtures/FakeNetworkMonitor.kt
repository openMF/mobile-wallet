/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.fixtures

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkChangeEvent
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Test double for [NetworkMonitor]. Exposes a mutable [networkStatus] flow for deterministic
 * network-condition simulation in unit and integration tests.
 *
 * Shared across all store test modules — use this instead of duplicating a private class per file.
 *
 * ```kotlin
 * val monitor = FakeNetworkMonitor(NetworkStatus.Unavailable)
 * monitor.setStatus(NetworkStatus.Available(onlineInfo))
 * ```
 */
class FakeNetworkMonitor(initialStatus: NetworkStatus) : NetworkMonitor {

    private val _networkStatus = MutableStateFlow(initialStatus)

    override val networkStatus: StateFlow<NetworkStatus> = _networkStatus.asStateFlow()

    override val isOnline: StateFlow<Boolean> =
        MutableStateFlow(initialStatus is NetworkStatus.Available).asStateFlow()

    override val networkChanges: SharedFlow<NetworkChangeEvent> =
        MutableSharedFlow<NetworkChangeEvent>().asSharedFlow()

    fun setStatus(status: NetworkStatus) {
        _networkStatus.value = status
    }

    override fun close() = Unit
}
