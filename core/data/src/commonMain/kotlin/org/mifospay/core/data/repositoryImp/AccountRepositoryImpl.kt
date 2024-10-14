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
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.network.FineractApiManager

class AccountRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : AccountRepository {

    override fun getTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<Result<Transaction>> {
        return apiManager.accountTransfersApi
            .getTransaction(accountId, transactionId)
            .map { it.toModel() }
            .asResult().flowOn(ioDispatcher)
    }

    override fun getAccountTransfer(transferId: Long): Flow<Result<TransferDetail>> {
        return apiManager.accountTransfersApi
            .getAccountTransfer(transferId.toInt())
            .asResult().flowOn(ioDispatcher)
    }
}
