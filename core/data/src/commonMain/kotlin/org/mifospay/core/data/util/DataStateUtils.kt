/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState

suspend fun <T> runAsDataState(
    context: CoroutineDispatcher? = null,
    block: suspend () -> T,
): DataState<T> =
    try {
        if (context != null) {
            DataState.Success(withContext(context) { block() })
        } else {
            DataState.Success(block())
        }
    } catch (e: Throwable) {
        DataState.Error(e as Exception)
    }

suspend fun <T> runAsDataState(
    networkMonitor: NetworkMonitor,
    context: CoroutineDispatcher? = null,
    block: suspend () -> T,
): DataState<T> {
    if (!networkMonitor.isOnline.first()) {
        return DataState.Error(Exception("Network unavailable"))
    }
    return runAsDataState(context, block)
}
