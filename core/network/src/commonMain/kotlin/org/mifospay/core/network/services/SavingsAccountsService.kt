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
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.entity.Page
import org.mifospay.core.model.entity.accounts.savings.SavingAccount
import org.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.core.model.entity.accounts.savings.Transactions
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.model.GenericResponse

interface SavingsAccountsService {
    @GET(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    fun getSavingsWithAssociations(
        @Path("accountId") accountId: Long,
        @Query("associations") associationType: String,
    ): Flow<SavingsWithAssociations>

    @GET(ApiEndPoints.SAVINGS_ACCOUNTS)
    fun getSavingsAccounts(
        @Query("limit") limit: Int,
    ): Flow<Page<SavingsWithAssociations>>

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS)
    fun createSavingsAccount(@Body savingAccount: SavingAccount): Flow<GenericResponse>

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    fun blockUnblockAccount(
        @Path("accountId") accountId: Long,
        @Query("command") command: String?,
    ): Flow<GenericResponse>

    @GET(
        ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}/" + ApiEndPoints.TRANSACTIONS +
                "/{transactionId}",
    )
    fun getSavingAccountTransaction(
        @Path("accountId") accountId: Long,
        @Path("transactionId") transactionId: Long,
    ): Flow<Transactions>

    @POST(
        ApiEndPoints.SAVINGS_ACCOUNTS +
                "/{accountId}/" + ApiEndPoints.TRANSACTIONS + "?command=deposit",
    )
    fun payViaMobile(@Path("accountId") accountId: Long): Flow<Transactions>
}

