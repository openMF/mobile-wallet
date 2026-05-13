/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.HttpStatusException
import org.mifospay.core.common.NetworkException
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.common.combineResultsWith
import org.mifospay.core.data.mapper.toAccount
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.mapper.toModelAccountType
import org.mifospay.core.data.mapper.toTransactionList
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.data.util.parseMifosError
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.AccountContent
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
            .asDataStateFlow(parseMifosError).flowOn(dispatcher)
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

    override fun getActiveAccountsWithTransactionsPerAccount(
        clientId: Long,
        limit: Int?,
    ): Flow<DataState<Map<Account, List<Transaction>>>> {
        val accounts = apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { entity -> entity.savingsAccounts.filter { it.status.active } }
            .map { it.toAccount() }
            .flowOn(dispatcher)

        return accounts.flatMapLatest { accountList ->
            val flows = accountList.map { account ->
                getTransactions(listOf(account.id), limit)
                    .map { transactions -> account to transactions }
            }

            combine(flows) { pairs ->
                pairs.toMap()
            }
        }.asDataStateFlow()
    }

    override fun getActiveAccounts(
        clientId: Long,
    ): Flow<DataState<List<Account>>> {
        return apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { entity -> entity.savingsAccounts.filter { it.status.active } }
            .map { it.toAccount() }
            .flowOn(dispatcher)
            .asDataStateFlow()
    }

    override fun getActiveAccountsWithAccountTransferTemplate(
        clientId: Long,
    ): Flow<DataState<List<Account>>> {
        val accountsFlow = apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { entity -> entity.savingsAccounts.filter { it.status.active } }
            .flowOn(dispatcher)

        val templateFlow = apiManager.accountTransfersApi
            .getAccountTransferTemplate()
            .flowOn(dispatcher)

        return accountsFlow.zip(templateFlow) { accounts, template ->
            accounts
                .toAccount()
                .map { account ->
                    val templateAccount = template.fromAccountOptions?.firstOrNull {
                        it.accountNo == account.number
                    }
                    if (templateAccount == null) {
                        account
                    } else {
                        account.copy(
                            clientName = templateAccount.clientName ?: "",
                            accountType = templateAccount.accountType?.toModelAccountType(),
                            officeName = templateAccount.officeName,
                            officeId = templateAccount.officeId,
                        )
                    }
                }
        }.asDataStateFlow()
    }

    override fun getTransactions(accountId: List<Long>, limit: Int?): Flow<List<Transaction>> {
        return accountId.asFlow().flatMapMerge { clientId ->
            getSelfAccountTransactions(clientId)
        }.scan(emptyList()) { acc, transactions ->
            (acc + transactions).sortedByDescending { transaction ->
                DateHelper.parseDateToMillis(transaction.date) ?: Long.MIN_VALUE
            }.let { sortedList ->
                limit?.let { sortedList.take(it) } ?: sortedList
            }
        }
    }

    override fun getTransactions(accountId: Long, limit: Int?): Flow<DataState<List<Transaction>>> {
        return apiManager.savingAccountsListApi
            .getSavingsWithAssociations(accountId, Constants.TRANSACTIONS)
            .map {
                if (limit != null) {
                    it.toTransactionList().take(limit)
                } else {
                    it.toTransactionList()
                }
            }
            .flowOn(dispatcher)
            .asDataStateFlow()
    }

    override fun getAccountsTransactions(
        clientId: Long,
    ): Flow<DataState<List<Transaction>>> {
        return apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { it.toAccount() }
            .map { list -> list.filter { it.status.active } }
            .map { list -> list.map { it.id } }
            .flatMapLatest { accountIds ->
                if (accountIds.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    getTransactions(accountId = accountIds, null)
                        .filter { transactions -> transactions.isNotEmpty() }
                }
            }
            .asDataStateFlow()
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
        } catch (e: ClientRequestException) {
            val status = e.response.status.value
            val responseBody = try {
                e.response.bodyAsText()
            } catch (_: Exception) {
                ""
            }
            val userMessage = parseMifosError(responseBody, status)
            DataState.Error(HttpStatusException(status, userMessage, e.message))
        } catch (e: ServerResponseException) {
            val status = e.response.status.value
            val responseBody = try {
                e.response.bodyAsText()
            } catch (_: Exception) {
                ""
            }
            val userMessage = parseMifosError(responseBody, status)
            DataState.Error(HttpStatusException(status, userMessage, e.message))
        } catch (e: IOException) {
            DataState.Error(NetworkException("Network unavailable. Please check your connection."))
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
