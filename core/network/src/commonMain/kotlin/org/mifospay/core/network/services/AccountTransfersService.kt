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

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.entity.accounts.savings.TransferDetail
import org.mifospay.core.network.utils.ApiEndPoints

/**
 * Created by ankur on 05/June/2018
 */
interface AccountTransfersService {
    @GET(ApiEndPoints.ACCOUNT_TRANSFER + "/{transferId}")
    suspend fun getAccountTransfer(@Path("transferId") transferId: Long): Flow<TransferDetail>
}
