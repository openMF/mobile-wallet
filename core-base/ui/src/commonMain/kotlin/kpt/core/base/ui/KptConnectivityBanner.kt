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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.compose.ConnectivityBanner
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.compose.rememberNetworkMonitor

/**
 * Thin wrapper around [ConnectivityBanner] from `cmp-network-monitor-compose`.
 *
 * Isolates the library import inside `core-base/ui` (which already depends on
 * `cmp-network-monitor-compose`) so that modules higher in the graph (e.g.,
 * `cmp-navigation`) can show a connectivity banner without needing a direct
 * dependency on the library artifact.
 *
 * Placed via [KptRootScaffold]'s `utilityBar` slot at the authenticated root.
 * Slides in from the top when the device goes offline; slides out when back online.
 *
 * @param showBanner Set to `false` to suppress the banner entirely — e.g. for apps
 *   without `INTERNET` permission or forks that surface connectivity via a different UI.
 * @param debounceMs Debounce applied to the underlying online/offline state transition.
 * @param message Text shown in the banner when offline.
 * @param icon Optional leading icon displayed before the message.
 * @param trailingContent Optional composable slot after the message — e.g. a "Retry"
 *   [TextButton] or a [CircularProgressIndicator].
 * @param monitor The [NetworkMonitor] to observe. Defaults to the process-global singleton.
 *   Override in tests by passing a fake that lets you toggle online/offline state.
 * @param modifier Modifier forwarded to [ConnectivityBanner].
 */
@Composable
fun KptConnectivityBanner(
    modifier: Modifier = Modifier,
    showBanner: Boolean = true,
    // 300ms mirrors NetworkMonitorContract.DEFAULT_DEBOUNCE_MS — suppresses WiFi↔Cell handoff flicker
    debounceMs: Long = 300L,
    message: String = "No internet connection",
    icon: ImageVector? = null,
    monitor: NetworkMonitor = rememberNetworkMonitor(),
    trailingContent: (@Composable () -> Unit)? = null,
) {
    if (showBanner) {
        ConnectivityBanner(
            modifier = modifier,
            debounceMs = debounceMs,
            message = message,
            icon = icon,
            trailingContent = trailingContent,
            monitor = monitor,
        )
    }
}
