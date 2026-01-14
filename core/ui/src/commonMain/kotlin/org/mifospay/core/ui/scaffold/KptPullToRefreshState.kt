/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui.scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Data class representing the pull-to-refresh state and behavior.
 *
 * @param isRefreshing Indicates whether the content is currently being
 *    refreshed.
 * @param onRefresh Callback triggered when a pull-to-refresh gesture is
 *    performed.
 */
data class KptPullToRefreshState(
    val isEnabled: Boolean,
    val isRefreshing: Boolean,
    val onRefresh: () -> Unit,
)

/**
 * Remembers and returns a [KptPullToRefreshState] instance.
 *
 * @param isRefreshing Whether the refresh animation should be shown.
 *    *(Default: `false`)*
 * @param onRefresh Callback to execute on pull-to-refresh. *(Default:
 *    empty lambda)*
 */
@Composable
fun rememberKptPullToRefreshState(
    isEnabled: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = { },
) = remember(isEnabled, isRefreshing, onRefresh) {
    KptPullToRefreshState(
        isEnabled = isEnabled,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    )
}
