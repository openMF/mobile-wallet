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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifospay.core.common.Constants
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.mapper.toSavingDetail
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.model.savingsaccount.CreateNewSavingEntity
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.model.savingsaccount.SavingAccountTemplate
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionsEntity
import org.mifospay.core.model.savingsaccount.UpdateSavingAccountEntity
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.entity.Page

class SavingsAccountRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : SavingsAccountRepository {
    override suspend fun getSavingsAccounts(
        limit: Int,
    ): Flow<DataState<Page<SavingsWithAssociationsEntity>>> {
        return apiManager.savingsAccountsApi
            .getSavingsAccounts(limit)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): Flow<DataState<SavingsWithAssociationsEntity>> {
        return apiManager.savingsAccountsApi
            .getSavingsWithAssociations(accountId, associationType)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getAccountDetail(accountId: Long): Flow<DataState<SavingAccountDetail>> {
        return apiManager.savingsAccountsApi
            .getSavingsWithAssociations(accountId, Constants.TRANSACTIONS)
            .catch { DataState.Error(it, null) }
            .map(SavingsWithAssociationsEntity::toSavingDetail)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun createSavingsAccount(
        savingAccount: CreateNewSavingEntity,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingsAccountsApi.createSavingsAccount(savingAccount)
            }

            DataState.Success("Savings Account Created Successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateSavingsAccount(
        accountId: Long,
        savingAccount: UpdateSavingAccountEntity,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingsAccountsApi.updateSavingsAccount(accountId, savingAccount)
            }

            DataState.Success("Savings Account Updated Successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun unblockAccount(
        accountId: Long,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingsAccountsApi.blockUnblockAccount(accountId, "unblock")
            }

            DataState.Success("Account unblocked successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun blockAccount(accountId: Long): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingsAccountsApi.blockUnblockAccount(accountId, "block")
            }

            DataState.Success("Account blocked successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getSavingAccountTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<DataState<Transaction>> {
        return apiManager.savingsAccountsApi
            .getSavingAccountTransaction(accountId, transactionId)
            .map(TransactionsEntity::toModel)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun payViaMobile(accountId: Long): Flow<DataState<Transaction>> {
        return apiManager.savingsAccountsApi
            .payViaMobile(accountId)
            .map(TransactionsEntity::toModel)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getSavingAccountTemplate(clientId: Long): Flow<DataState<SavingAccountTemplate>> {
        return apiManager.savingsAccountsApi
            .getSavingAccountTemplate(clientId)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }
}
