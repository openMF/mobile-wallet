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

import com.mifospay.core.model.entity.accounts.savings.Transactions
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.GenericResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface SavingsAccountsService {

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    fun blockUnblockAccount(
        @Path("accountId") accountId: Long,
        @Query("command") command: String?,
    ): Observable<GenericResponse>

    @GET(
        ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}/" + ApiEndPoints.TRANSACTIONS +
            "/{transactionId}",
    )
    fun getSavingAccountTransaction(
        @Path("accountId") accountId: Long,
        @Path("transactionId") transactionId: Long,
    ): Observable<Transactions>
}
