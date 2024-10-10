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
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.entity.TPTResponse
import org.mifospay.core.network.model.entity.payload.TransferPayload
import org.mifospay.core.network.model.entity.templates.account.AccountOptionsTemplate

class ThirdPartyTransferRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : ThirdPartyTransferRepository {
    override suspend fun getTransferTemplate(): Flow<Result<AccountOptionsTemplate>> {
        return apiManager.thirdPartyTransferApi
            .accountTransferTemplate()
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun makeTransfer(payload: TransferPayload): Flow<Result<TPTResponse>> {
        return apiManager.thirdPartyTransferApi
            .makeTransfer(payload)
            .asResult().flowOn(ioDispatcher)
    }
}
