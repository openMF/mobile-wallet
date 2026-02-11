/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.OfficeRepository
import org.mifospay.core.model.office.Office
import org.mifospay.core.network.FineractApiManager

class OfficeRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : OfficeRepository {
    override fun getOffices(): Flow<DataState<List<Office>>> = flow {
        emit(DataState.Loading)

        apiManager.officeApi.getOffices()
            .retryWhen { _, attempt ->
                if (attempt < 3) {
                    delay(1000L * (1 shl attempt.toInt()))
                    true
                } else {
                    false
                }
            }
            .collect { offices ->
                emit(DataState.Success(offices))
            }
    }
        .catch { exception ->
            emit(DataState.Error(exception, null))
        }
        .flowOn(ioDispatcher)
}
