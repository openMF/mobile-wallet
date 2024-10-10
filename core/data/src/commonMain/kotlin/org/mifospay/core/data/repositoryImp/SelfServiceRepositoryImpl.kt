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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.mapper.toAccount
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.mapper.toTransactionList
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.authentication.AuthenticationPayload
import org.mifospay.core.network.model.entity.beneficary.Beneficiary
import org.mifospay.core.network.model.entity.beneficary.BeneficiaryPayload
import org.mifospay.core.network.model.entity.beneficary.BeneficiaryUpdatePayload
import org.mifospay.core.network.model.entity.user.User

class SelfServiceRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val dispatcher: CoroutineDispatcher,
) : SelfServiceRepository {

    override suspend fun loginSelf(payload: AuthenticationPayload): Result<User> {
        return try {
            val result = apiManager.authenticationApi.authenticate(payload)

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getSelfClientDetails(clientId: Long): Result<Client> {
        return try {
            val result = apiManager.clientsApi.getClientForId(clientId).toModel()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getSelfClientDetails(): Flow<Result<Page<Client>>> {
        return apiManager.clientsApi.clients().map { it.toModel() }.asResult().flowOn(dispatcher)
    }

    override fun getSelfAccountTransactions(
        accountId: Long,
    ): Flow<List<Transaction>> {
        return flow {
            try {
                val result = withContext(dispatcher) {
                    apiManager.savingAccountsListApi
                        .getSavingsWithAssociations(accountId, Constants.TRANSACTIONS)
                }

                emit(result.toTransactionList())
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long,
    ): Result<Flow<Transaction>> {
        return try {
            val result = withContext(dispatcher) {
                apiManager.savingAccountsListApi.getSavingAccountTransaction(
                    accountId,
                    transactionId,
                )
            }

            Result.Success(result.map { it.toModel() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getSelfAccounts(clientId: Long): Result<List<Account>> {
        return try {
            val result = withContext(dispatcher) {
                apiManager.clientsApi.getAccounts(clientId, Constants.SAVINGS)
            }

            Result.Success(result.toAccount())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBeneficiaryList(): Flow<Result<List<Beneficiary>>> {
        return apiManager.beneficiaryApi.beneficiaryList().asResult().flowOn(dispatcher)
    }

    override suspend fun createBeneficiary(
        beneficiaryPayload: BeneficiaryPayload,
    ): Flow<Result<CommonResponse>> {
        return apiManager.beneficiaryApi
            .createBeneficiary(beneficiaryPayload)
            .asResult().flowOn(dispatcher)
    }

    override suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): Flow<Result<CommonResponse>> {
        return apiManager.beneficiaryApi
            .updateBeneficiary(beneficiaryId, payload)
            .asResult().flowOn(dispatcher)
    }
}
