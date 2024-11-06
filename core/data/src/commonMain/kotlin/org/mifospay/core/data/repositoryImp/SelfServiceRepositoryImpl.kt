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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.common.combineResultsWith
import org.mifospay.core.data.mapper.toAccount
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.mapper.toTransactionList
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.AccountContent
import org.mifospay.core.model.account.AccountsWithTransactions
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.authentication.AuthenticationPayload
import org.mifospay.core.network.model.entity.user.User

@OptIn(ExperimentalCoroutinesApi::class)
class SelfServiceRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val dispatcher: CoroutineDispatcher,
) : SelfServiceRepository {
    override suspend fun loginSelf(payload: AuthenticationPayload): DataState<User> {
        return try {
            val result = apiManager.authenticationApi.authenticate(payload)

            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override fun getSelfClientDetails(clientId: Long): Flow<DataState<Client>> {
        return apiManager.clientsApi
            .getClient(clientId)
            .onStart { DataState.Loading }
            .catch { DataState.Error(it, null) }
            .map { it.toModel() }
            .asDataStateFlow().flowOn(dispatcher)
    }

    override suspend fun getSelfClientDetails(): Flow<DataState<Page<Client>>> {
        return apiManager.clientsApi.clients().map { it.toModel() }.asDataStateFlow()
            .flowOn(dispatcher)
    }

    override fun getSelfAccountTransactions(
        accountId: Long,
    ): Flow<List<Transaction>> {
        return apiManager.savingAccountsListApi
            .getSavingsWithAssociations(accountId, Constants.TRANSACTIONS)
            .map { it.toTransactionList() }
            .flowOn(dispatcher)
    }

    override suspend fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long,
    ): DataState<Flow<Transaction>> {
        return try {
            val result = withContext(dispatcher) {
                apiManager.savingAccountsListApi.getSavingAccountTransaction(
                    accountId,
                    transactionId,
                )
            }

            DataState.Success(result.map { it.toModel() })
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override fun getSelfAccounts(clientId: Long): Flow<DataState<List<Account>>> {
        return apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { it.toAccount() }
            .asDataStateFlow().flowOn(dispatcher)
    }

    override fun getAccountAndBeneficiaryList(clientId: Long): Flow<DataState<AccountContent>> {
        val accountList = apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .onStart { DataState.Loading }
            .catch { DataState.Error(it, null) }
            .map { DataState.Success(it.toAccount()) }
            .flowOn(dispatcher)

        val beneficiaryList = apiManager.beneficiaryApi
            .beneficiaryList()
            .onStart { DataState.Loading }
            .catch { DataState.Error(it, null) }
            .map { DataState.Success(it) }
            .flowOn(dispatcher)

        return accountList.zip(beneficiaryList) { accounts, beneficiaries ->
            accounts.combineResultsWith(beneficiaries) { accData, bccData ->
                AccountContent(accData, bccData)
            }
        }.flowOn(dispatcher)
    }

    // TODO:: Optimize below functions
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getActiveAccountsWithTransactions(
        clientId: Long,
        limit: Int,
    ): Flow<DataState<AccountsWithTransactions>> {
        val accounts = apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { entity -> entity.savingsAccounts.filter { it.status.active } }
            .map { it.toAccount() }
            .flowOn(dispatcher)

        val transactions = accounts
            .map { list -> list.map { it.id } }
            .flatMapLatest {
                getTransactions(it, limit)
            }

        return accounts.combine(transactions) { accountList, transaction ->
            AccountsWithTransactions(accountList, transaction)
        }.map { DataState.Success(it) }.flowOn(dispatcher)
    }

    override fun getTransactions(accountId: List<Long>, limit: Int?): Flow<List<Transaction>> {
        return accountId.asFlow().flatMapMerge { clientId ->
            getSelfAccountTransactions(clientId)
        }.scan(emptyList()) { acc, transactions ->
            acc + transactions.sortedByDescending { it.date }.let { sortedList ->
                limit?.let { sortedList.take(it) } ?: sortedList
            }
        }
    }

    override fun getAccountsTransactions(
        clientId: Long,
    ): Flow<DataState<List<Transaction>>> {
        return apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .onStart { DataState.Loading }
            .catch { DataState.Error(it, null) }
            .map { it.toAccount() }
            .map { list -> list.filter { it.status.active } }
            .map { list -> list.map { it.id } }
            .flatMapLatest {
                getTransactions(accountId = it, null)
            }.map {
                DataState.Success(it)
            }
            .flowOn(dispatcher)
    }

    override fun getBeneficiaryList(): Flow<DataState<List<Beneficiary>>> {
        return apiManager.beneficiaryApi.beneficiaryList().asDataStateFlow().flowOn(dispatcher)
    }

    override suspend fun createBeneficiary(
        beneficiaryPayload: BeneficiaryPayload,
    ): DataState<String> {
        return try {
            withContext(dispatcher) {
                apiManager.beneficiaryApi.createBeneficiary(beneficiaryPayload)
            }

            DataState.Success("Beneficiary created successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): DataState<String> {
        return try {
            withContext(dispatcher) {
                apiManager.beneficiaryApi.updateBeneficiary(beneficiaryId, payload)
            }

            DataState.Success("Beneficiary updated successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteBeneficiary(beneficiaryId: Long): DataState<String> {
        return try {
            withContext(dispatcher) {
                apiManager.beneficiaryApi.deleteBeneficiary(beneficiaryId)
            }

            DataState.Success("Beneficiary deleted successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
