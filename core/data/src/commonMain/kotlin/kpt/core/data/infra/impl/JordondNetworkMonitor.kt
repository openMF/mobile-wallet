/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.infra.impl

import dev.jordond.connectivity.Connectivity
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkChangeEvent
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkInfo
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kpt.core.data.infra.NetworkMonitor as KptNetworkMonitor

/**
 * Cross-platform [KptNetworkMonitor] backed by `dev.jordond.connectivity` 2.5.0.
 *
 * Fixes the kmptoolkit `cmp-network-monitor` v3.5.3 **initial-seed bug** (`isOnline` /
 * `networkStatus` stuck at their `false` / `Unavailable` seed when the device is already
 * connected at launch, because no `onAvailable` callback fires for the already-connected
 * network) — and fixes it on **every** target, not just Android. Because every Store5 read
 * gates on `networkStatus` via `asScreenStream` → DecisionEngine, that seed bug shows
 * `NoNetwork` on-device even with validated connectivity.
 *
 * jordond seeds the **current** state up-front via a one-shot [Connectivity.status] check
 * and then tracks [Connectivity.statusUpdates], satisfying `NetworkMonitorContract`
 * invariant #2 ("initial value MUST reflect the platform's current state") on all
 * platforms. The engine is chosen per source set by [platformConnectivity]: native device
 * callbacks on Android + iOS (`connectivity-device`), URL polling on Desktop / JS / WasmJs
 * (`connectivity-http`).
 *
 * `networkChanges` is unused by the Store layer (it consumes only `networkStatus` and its
 * debounced derivation), so an empty [SharedFlow] satisfies the contract. jordond's `Status`
 * exposes online + metered only, so `NetworkType` degrades to `Unknown` — the DecisionEngine
 * only type-checks `is NetworkStatus.Available`, so connection-type detail is not consumed.
 *
 * TEMPLATE-DERIVED root cause (RULE-TEMPLATE-MODULE-FIX-UPSTREAM-001): the real fix belongs
 * in `cmp-network-monitor` (MobileByteLabs/KmpToolkit). This proves the jordond engine on
 * all six targets before it is ported upstream; once a fixed kmptoolkit ships, this monitor
 * and the jordond dependency can be removed and the binding reverts to `install()`.
 */
class JordondNetworkMonitor(
    private val connectivity: Connectivity,
    scope: CoroutineScope,
) : KptNetworkMonitor {

    init {
        // Idempotent — options request autoStart, this guarantees monitoring even if a
        // provider was created without it.
        connectivity.start()
    }

    // Correct seed first (one-shot current state), then follow live updates.
    private val statuses: Flow<Connectivity.Status> = flow {
        emit(connectivity.status())
        emitAll(connectivity.statusUpdates)
    }

    override val isOnline: StateFlow<Boolean> =
        statuses.map { it.isConnected }.stateIn(scope, SharingStarted.Eagerly, true)

    override val networkStatus: StateFlow<NetworkStatus> =
        statuses.map { it.toNetworkStatus() }.stateIn(scope, SharingStarted.Eagerly, ONLINE)

    override val networkChanges: SharedFlow<NetworkChangeEvent> = MutableSharedFlow()

    override fun close() = connectivity.stop()

    private fun Connectivity.Status.toNetworkStatus(): NetworkStatus =
        if (isConnected) {
            NetworkStatus.Available(NetworkInfo(NetworkType.Unknown, isMetered, 0, 0))
        } else {
            NetworkStatus.Unavailable
        }

    private companion object {
        // Contract fallback: assume online until the first real emission corrects it.
        val ONLINE: NetworkStatus = NetworkStatus.Available(
            NetworkInfo(NetworkType.Unknown, false, 0, 0),
        )
    }
}
