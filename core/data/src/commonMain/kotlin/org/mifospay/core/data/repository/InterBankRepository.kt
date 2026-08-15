/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repository

import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.model.interbank.InterBankParticipantResponse
import org.mifospay.core.model.interbank.InterBankPartyInfoResponse
import org.mifospay.core.model.interbank.InterBankTransferRequest
import org.mifospay.core.model.interbank.InterBankTransferResponse

interface InterBankRepository {
    suspend fun fetchParticipant(
        partyId: String,
        currencyCode: String,
    ): InterBankParticipantResponse

    suspend fun fetchPartyInfo(
        partyId: String,
        currencyCode: String,
        ownerFspId: String,
    ): InterBankPartyInfoResponse

    fun findParticipant(
        partyId: String,
        currencyCode: String,
    ): ScreenStateStream<InterBankPartyInfoResponse>

    suspend fun interBankMakeTransfer(
        request: InterBankTransferRequest,
    ): InterBankTransferResponse
}
