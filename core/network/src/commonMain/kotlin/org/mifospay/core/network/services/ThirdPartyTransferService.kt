/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.services

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.network.model.entity.TPTResponse
import org.mifospay.core.network.model.entity.payload.TransferPayload
import org.mifospay.core.network.model.entity.templates.account.AccountOptionsTemplate
import org.mifospay.core.network.utils.ApiEndPoints

interface ThirdPartyTransferService {
    @GET(ApiEndPoints.ACCOUNT_TRANSFER + "/template?type=tpt")
    suspend fun accountTransferTemplate(): Flow<AccountOptionsTemplate>

    @POST(ApiEndPoints.ACCOUNT_TRANSFER + "?type=tpt")
    suspend fun makeTransfer(@Body transferPayload: TransferPayload): Flow<TPTResponse>
}
