/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.data.util.parseMifosError
import org.mifospay.core.model.network.entity.TPTResponse
import org.mifospay.core.model.network.entity.payload.TransferPayload
import org.mifospay.core.model.network.entity.templates.account.AccountOptionsTemplate
import org.mifospay.core.network.SelfServiceApiManager

class ThirdPartyTransferRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : ThirdPartyTransferRepository {
    override suspend fun getTransferTemplate(): AccountOptionsTemplate {
        return apiManager.thirdPartyTransferApi
            .accountTransferTemplate()
    }

    override fun makeTransfer(payload: TransferPayload): ScreenStateStream<TPTResponse> {
        return flow { emit(apiManager.thirdPartyTransferApi.makeTransfer(payload)) }
            .asScreenStateFlow(errorBodyParser = parseMifosError)
            .flowOn(ioDispatcher)
    }
}
