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
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.mapper.toAccount
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.AccountTransferPayload
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.network.FineractApiManager

class AccountRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : AccountRepository {

    override fun getTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<DataState<Transaction>> {
        return apiManager.accountTransfersApi
            .getTransaction(accountId, transactionId)
            .map { it.toModel() }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getAccountTransfer(transferId: Long): Flow<DataState<TransferDetail>> {
        return apiManager.accountTransfersApi
            .getAccountTransfer(transferId.toInt())
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun searchAccounts(query: String): Flow<DataState<List<AccountResult>>> {
        return apiManager.accountTransfersApi
            .searchAccounts(query, "savings")
            .catch { DataState.Error(it, null) }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getSelfAccounts(clientId: Long): Flow<DataState<List<Account>>> {
        return apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { it.toAccount() }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun makeTransfer(payload: AccountTransferPayload): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.accountTransfersApi.makeTransfer(payload)
            }

            DataState.Success("Transaction Successful")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }
}
