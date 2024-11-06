/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.model.savingsaccount.CreateNewSavingEntity
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.model.savingsaccount.SavingAccountTemplate
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.UpdateSavingAccountEntity
import org.mifospay.core.network.model.entity.Page

interface SavingsAccountRepository {
    suspend fun getSavingsAccounts(limit: Int): Flow<DataState<Page<SavingsWithAssociationsEntity>>>

    suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): Flow<DataState<SavingsWithAssociationsEntity>>

    fun getAccountDetail(accountId: Long): Flow<DataState<SavingAccountDetail>>

    suspend fun createSavingsAccount(savingAccount: CreateNewSavingEntity): DataState<String>

    suspend fun updateSavingsAccount(
        accountId: Long,
        savingAccount: UpdateSavingAccountEntity,
    ): DataState<String>

    suspend fun unblockAccount(
        accountId: Long,
    ): DataState<String>

    suspend fun blockAccount(
        accountId: Long,
    ): DataState<String>

    suspend fun getSavingAccountTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<DataState<Transaction>>

    suspend fun payViaMobile(accountId: Long): Flow<DataState<Transaction>>

    fun getSavingAccountTemplate(clientId: Long): Flow<DataState<SavingAccountTemplate>>
}
