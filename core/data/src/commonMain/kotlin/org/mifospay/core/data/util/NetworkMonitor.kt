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

import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting app connectivity status
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}

/**
 * Wraps an [upstream] [DataState] [Flow] with a reactive network guard.
 */
fun <T> NetworkMonitor.withNetworkCheck(
    upstream: Flow<org.mifospay.core.common.DataState<T>>,
): Flow<org.mifospay.core.common.DataState<T>> = kotlinx.coroutines.flow.combine(isOnline, upstream) { isOnline, dataState ->
    when {
        dataState is org.mifospay.core.common.DataState.Success -> dataState
        dataState is org.mifospay.core.common.DataState.Loading -> dataState
        !isOnline -> org.mifospay.core.common.DataState.Error(Exception("Network unavailable"))
        else -> dataState
    }
}
