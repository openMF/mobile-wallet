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
import org.mifospay.core.common.Result
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.accounts.savings.SavingAccountEntity
import org.mifospay.core.network.model.entity.accounts.savings.SavingsWithAssociationsEntity

interface SavingsAccountRepository {
    suspend fun getSavingsAccounts(limit: Int): Flow<Result<Page<SavingsWithAssociationsEntity>>>

    suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): Flow<Result<SavingsWithAssociationsEntity>>

    suspend fun createSavingsAccount(savingAccount: SavingAccountEntity): Flow<Result<GenericResponse>>

    suspend fun unblockAccount(
        accountId: Long,
    ): Result<String>

    suspend fun blockAccount(
        accountId: Long,
    ): Result<String>

    suspend fun getSavingAccountTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<Result<Transaction>>

    suspend fun payViaMobile(accountId: Long): Flow<Result<Transaction>>
}
