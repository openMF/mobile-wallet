/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.analytics

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kpt.core.base.analytics.AnalyticsHelper

/**
 * Bridges `NetworkMonitor.networkStatus` transitions to [AnalyticsHelper] as
 * named events. Useful for diagnosing reports of "the app doesn't refresh
 * after I reconnect" — the event stream confirms whether the OS-level
 * transition reached the app.
 *
 * Emits one of:
 * - `network.transition.online_to_offline`
 * - `network.transition.offline_to_online`
 * - `network.transition.captive_portal_detected`
 * - `network.transition.captive_portal_resolved`
 *
 * Wire once at app startup via Koin:
 * ```kotlin
 * single {
 *     NetworkTelemetry(get(), get<AnalyticsHelper>()).also { it.start(get()) }
 * }
 * ```
 *
 * Default is a no-op when the bound `AnalyticsHelper` is `NoOpAnalyticsHelper`
 * (release builds without Firebase wiring) — the transitions are still computed
 * but the events go nowhere. Replace the Koin binding with `FirebaseAnalyticsHelper`
 * (or fork's equivalent) to actually emit.
 */
class NetworkTelemetry(
    private val monitor: NetworkMonitor,
    private val analytics: AnalyticsHelper,
) {

    /**
     * Begin observing [monitor]'s networkStatus and logging transitions to
     * [analytics]. Returns the launched [Job] so callers can cancel if needed.
     *
     * The first emission is treated as the initial state — no event is logged
     * for it (there's no transition yet). Subsequent emissions are diffed
     * against the previous state.
     */
    fun start(scope: CoroutineScope): Job = monitor.networkStatus
        .runningFold<NetworkStatus, Pair<NetworkStatus?, NetworkStatus>>(
            initial = null to NetworkStatus.Unavailable,
        ) { acc, current -> acc.second to current }
        .onEach { (prev, curr) -> logTransition(prev, curr) }
        .launchIn(scope)

    private fun logTransition(prev: NetworkStatus?, curr: NetworkStatus) {
        if (prev == null) return // first emission — no transition yet

        when {
            prev is NetworkStatus.Available && curr is NetworkStatus.Unavailable ->
                analytics.logEvent(EVENT_ONLINE_TO_OFFLINE)

            prev is NetworkStatus.Unavailable && curr is NetworkStatus.Available ->
                analytics.logEvent(EVENT_OFFLINE_TO_ONLINE)

            curr is NetworkStatus.CaptivePortal && prev !is NetworkStatus.CaptivePortal ->
                analytics.logEvent(EVENT_CAPTIVE_PORTAL_DETECTED)

            prev is NetworkStatus.CaptivePortal && curr !is NetworkStatus.CaptivePortal ->
                analytics.logEvent(EVENT_CAPTIVE_PORTAL_RESOLVED)
        }
    }

    companion object {
        const val EVENT_ONLINE_TO_OFFLINE: String = "network.transition.online_to_offline"
        const val EVENT_OFFLINE_TO_ONLINE: String = "network.transition.offline_to_online"
        const val EVENT_CAPTIVE_PORTAL_DETECTED: String = "network.transition.captive_portal_detected"
        const val EVENT_CAPTIVE_PORTAL_RESOLVED: String = "network.transition.captive_portal_resolved"
    }
}
