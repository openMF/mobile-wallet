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
import org.mifospay.core.model.domain.Transaction
import org.mifospay.core.model.entity.Page
import org.mifospay.core.model.entity.accounts.savings.SavingAccount
import org.mifospay.core.model.entity.accounts.savings.SavingsWithAssociationsEntity
import org.mifospay.core.network.model.GenericResponse

interface SavingsAccountRepository {
    suspend fun getSavingsAccounts(limit: Int): Flow<Result<Page<SavingsWithAssociationsEntity>>>

    suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): Flow<Result<SavingsWithAssociationsEntity>>

    suspend fun createSavingsAccount(savingAccount: SavingAccount): Flow<Result<GenericResponse>>

    suspend fun blockUnblockAccount(accountId: Long, command: String?): Flow<Result<GenericResponse>>

    suspend fun getSavingAccountTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<Result<Transaction>>

    suspend fun payViaMobile(accountId: Long): Flow<Result<Transaction>>
}
