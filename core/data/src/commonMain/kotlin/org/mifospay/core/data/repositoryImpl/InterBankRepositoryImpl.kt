/*
 * Copyright 2025 Mifos Initiative
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
import kotlinx.coroutines.withContext
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.repository.InterBankRepository
import org.mifospay.core.model.interbank.InterBankParticipantRequest
import org.mifospay.core.model.interbank.InterBankParticipantResponse
import org.mifospay.core.model.interbank.InterBankPartyInfoRequest
import org.mifospay.core.model.interbank.InterBankPartyInfoResponse
import org.mifospay.core.model.interbank.InterBankTransferRequest
import org.mifospay.core.model.interbank.InterBankTransferResponse
import org.mifospay.core.network.InterBankApiManager

class InterBankRepositoryImpl(
    private val apiManager: InterBankApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : InterBankRepository {

    override suspend fun fetchParticipant(
        partyId: String,
        currencyCode: String,
    ): InterBankParticipantResponse = withContext(ioDispatcher) {
        val request = InterBankParticipantRequest(
            partyId = partyId.trim(),
            currencyCode = currencyCode,
            partyIdType = "MSISDN",
        )
        apiManager.interBankApi.fetchParticipant(request)
    }

    override suspend fun fetchPartyInfo(
        partyId: String,
        currencyCode: String,
        ownerFspId: String,
    ): InterBankPartyInfoResponse = withContext(ioDispatcher) {
        val request = InterBankPartyInfoRequest(
            partyId = partyId.trim(),
            currencyCode = currencyCode,
            ownerFspId = ownerFspId,
            partyIdType = "MSISDN",
        )
        apiManager.interBankApi.fetchPartyInfo(request)
    }

    override fun findParticipant(
        partyId: String,
        currencyCode: String,
    ): ScreenStateStream<InterBankPartyInfoResponse> {
        return flow {
            // First, fetch participant to get the FSP ID, then fetch party info.
            val participant = fetchParticipant(partyId, currencyCode)
            val partyInfo = fetchPartyInfo(
                partyId = partyId,
                currencyCode = currencyCode,
                ownerFspId = participant.fspId,
            )
            emit(partyInfo)
        }.asScreenStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun interBankMakeTransfer(
        request: InterBankTransferRequest,
    ): InterBankTransferResponse = withContext(ioDispatcher) {
        apiManager.interBankApi.interBankMakeTransfer(request)
    }
}
