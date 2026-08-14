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
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
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
    ): DataState<InterBankParticipantResponse> {
        return try {
            val request = InterBankParticipantRequest(
                partyId = partyId.trim(),
                currencyCode = currencyCode,
                partyIdType = "MSISDN",
            )
            val result = withContext(ioDispatcher) {
                apiManager.interBankApi.fetchParticipant(request)
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun fetchPartyInfo(
        partyId: String,
        currencyCode: String,
        ownerFspId: String,
    ): DataState<InterBankPartyInfoResponse> {
        return try {
            val request = InterBankPartyInfoRequest(
                partyId = partyId.trim(),
                currencyCode = currencyCode,
                ownerFspId = ownerFspId,
                partyIdType = "MSISDN",
            )
            val result = withContext(ioDispatcher) {
                apiManager.interBankApi.fetchPartyInfo(request)
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun findParticipant(
        partyId: String,
        currencyCode: String,
    ): DataState<InterBankPartyInfoResponse> {
        return try {
            // First, fetch participant to get the FSP ID
            val participantResult = fetchParticipant(partyId, currencyCode)

            if (participantResult !is DataState.Success) {
                return DataState.Error(
                    Exception("Failed to fetch participant"),
                )
            }

            val participant = participantResult.data

            // Then, fetch party info using the FSP ID
            val partyInfoResult = fetchPartyInfo(
                partyId = partyId,
                currencyCode = currencyCode,
                ownerFspId = participant.fspId,
            )

            partyInfoResult
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun interBankMakeTransfer(
        request: InterBankTransferRequest,
    ): DataState<InterBankTransferResponse> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.interBankApi.interBankMakeTransfer(request)
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
