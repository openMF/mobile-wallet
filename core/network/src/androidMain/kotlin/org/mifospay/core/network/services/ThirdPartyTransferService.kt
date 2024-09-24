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

import com.mifospay.core.model.entity.TPTResponse
import com.mifospay.core.model.entity.payload.TransferPayload
import com.mifospay.core.model.entity.templates.account.AccountOptionsTemplate
import org.mifospay.core.network.ApiEndPoints
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable

/**
 * Created by dilpreet on 21/6/17.
 */
interface ThirdPartyTransferService {
    @get:GET(ApiEndPoints.ACCOUNT_TRANSFER + "/template?type=tpt")
    val accountTransferTemplate: Observable<AccountOptionsTemplate>

    @POST(ApiEndPoints.ACCOUNT_TRANSFER + "?type=\"tpt\"")
    fun makeTransfer(@Body transferPayload: TransferPayload): Observable<TPTResponse>
}
