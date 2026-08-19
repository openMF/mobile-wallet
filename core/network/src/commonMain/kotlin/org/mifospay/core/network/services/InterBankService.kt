/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.network.services

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import org.mifospay.core.model.interbank.InterBankParticipantRequest
import org.mifospay.core.model.interbank.InterBankParticipantResponse
import org.mifospay.core.model.interbank.InterBankPartyInfoRequest
import org.mifospay.core.model.interbank.InterBankPartyInfoResponse
import org.mifospay.core.model.interbank.InterBankTransferRequest
import org.mifospay.core.model.interbank.InterBankTransferResponse

interface InterBankService {
    @POST("participant")
    suspend fun fetchParticipant(
        @Body request: InterBankParticipantRequest,
    ): InterBankParticipantResponse

    @POST("partyinfo")
    suspend fun fetchPartyInfo(
        @Body request: InterBankPartyInfoRequest,
    ): InterBankPartyInfoResponse

    @POST("executetransfer")
    suspend fun interBankMakeTransfer(
        @Body request: InterBankTransferRequest,
    ): InterBankTransferResponse
}
