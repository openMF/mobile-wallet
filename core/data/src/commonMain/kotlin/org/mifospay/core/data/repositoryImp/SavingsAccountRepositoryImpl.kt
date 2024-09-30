/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.model.domain.Transaction
import org.mifospay.core.model.entity.Page
import org.mifospay.core.model.entity.accounts.savings.SavingAccount
import org.mifospay.core.model.entity.accounts.savings.SavingsWithAssociationsEntity
import org.mifospay.core.model.entity.accounts.savings.TransactionsEntity
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.GenericResponse

class SavingsAccountRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : SavingsAccountRepository {
    override suspend fun getSavingsAccounts(
        limit: Int,
    ): Flow<Result<Page<SavingsWithAssociationsEntity>>> {
        return apiManager.savingsAccountsApi
            .getSavingsAccounts(limit)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): Flow<Result<SavingsWithAssociationsEntity>> {
        return apiManager.savingsAccountsApi
            .getSavingsWithAssociations(accountId, associationType)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun createSavingsAccount(
        savingAccount: SavingAccount,
    ): Flow<Result<GenericResponse>> {
        return apiManager.savingsAccountsApi
            .createSavingsAccount(savingAccount)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun blockUnblockAccount(
        accountId: Long,
        command: String?,
    ): Flow<Result<GenericResponse>> {
        return apiManager.savingsAccountsApi
            .blockUnblockAccount(accountId, command)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun getSavingAccountTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<Result<Transaction>> {
        return apiManager.savingsAccountsApi
            .getSavingAccountTransaction(accountId, transactionId)
            .map(TransactionsEntity::toModel)
            .asResult()
            .flowOn(ioDispatcher)
    }

    override suspend fun payViaMobile(accountId: Long): Flow<Result<Transaction>> {
        return apiManager.savingsAccountsApi
            .payViaMobile(accountId)
            .map(TransactionsEntity::toModel)
            .asResult().flowOn(ioDispatcher)
    }
}
