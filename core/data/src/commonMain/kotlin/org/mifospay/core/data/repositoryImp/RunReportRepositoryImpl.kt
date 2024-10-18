/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.repository.RunReportRepository
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.network.FineractApiManager

class RunReportRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : RunReportRepository {
    override suspend fun getTransactionReceipt(
        outputType: String,
        transactionId: String,
    ): Flow<DataState<Transaction>> {
        return apiManager.runReportApi
            .getTransactionReceipt(outputType, transactionId)
            .map { it.toModel() }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }
}
