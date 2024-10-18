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
import org.mifospay.core.model.savingsaccount.BlockUnblockResponseEntity
import org.mifospay.core.model.savingsaccount.CreateNewSavingEntity
import org.mifospay.core.model.savingsaccount.SavingAccountTemplate
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity
import org.mifospay.core.model.savingsaccount.TransactionsEntity
import org.mifospay.core.model.savingsaccount.UpdateSavingAccountEntity
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.utils.ApiEndPoints

interface SavingsAccountsService {
    @GET(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    fun getSavingsWithAssociations(
        @Path("accountId") accountId: Long,
        @Query("associations") associationType: String,
    ): Flow<SavingsWithAssociationsEntity>

    @GET(ApiEndPoints.SAVINGS_ACCOUNTS)
    suspend fun getSavingsAccounts(
        @Query("limit") limit: Int,
    ): Flow<Page<SavingsWithAssociationsEntity>>

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS)
    suspend fun createSavingsAccount(@Body savingAccount: CreateNewSavingEntity)

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    suspend fun updateSavingsAccount(
        @Path("accountId") accountId: Long,
        @Body savingAccount: UpdateSavingAccountEntity,
    )

    @POST(ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}")
    suspend fun blockUnblockAccount(
        @Path("accountId") accountId: Long,
        @Query("command") command: String?,
    ): BlockUnblockResponseEntity

    @GET(
        ApiEndPoints.SAVINGS_ACCOUNTS + "/{accountId}/" + ApiEndPoints.TRANSACTIONS +
            "/{transactionId}",
    )
    suspend fun getSavingAccountTransaction(
        @Path("accountId") accountId: Long,
        @Path("transactionId") transactionId: Long,
    ): Flow<TransactionsEntity>

    @POST(
        ApiEndPoints.SAVINGS_ACCOUNTS +
            "/{accountId}/" + ApiEndPoints.TRANSACTIONS + "?command=deposit",
    )
    suspend fun payViaMobile(@Path("accountId") accountId: Long): Flow<TransactionsEntity>

    @GET(ApiEndPoints.SAVINGS_ACCOUNTS + "/template")
    fun getSavingAccountTemplate(@Query("clientId") clientId: Long): Flow<SavingAccountTemplate>
}
