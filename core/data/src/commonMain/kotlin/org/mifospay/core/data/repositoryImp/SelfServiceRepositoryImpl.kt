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
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.model.domain.client.Client
import org.mifospay.core.model.domain.user.User
import org.mifospay.core.model.entity.Page
import org.mifospay.core.model.entity.accounts.savings.SavingsWithAssociationsEntity
import org.mifospay.core.model.entity.accounts.savings.TransactionsEntity
import org.mifospay.core.model.entity.authentication.AuthenticationPayload
import org.mifospay.core.model.entity.beneficary.Beneficiary
import org.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import org.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import org.mifospay.core.model.entity.client.ClientAccounts
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.CommonResponse

class SelfServiceRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val dispatcher: CoroutineDispatcher,
) : SelfServiceRepository {

    override suspend fun loginSelf(payload: AuthenticationPayload): Flow<Result<User>> {
        return apiManager.authenticationApi.authenticate(payload).asResult().flowOn(dispatcher)
    }

    override suspend fun getSelfClientDetails(clientId: Long): Flow<Result<Client>> {
        return apiManager.clientsApi
            .getClientForId(clientId)
            .map { it.toModel() }
            .asResult().flowOn(dispatcher)
    }

    override suspend fun getSelfClientDetails(): Flow<Result<Page<Client>>> {
        return apiManager.clientsApi.clients().map { it.toModel() }.asResult().flowOn(dispatcher)
    }

    override suspend fun getSelfAccountTransactions(
        accountId: Long,
    ): Flow<Result<SavingsWithAssociationsEntity>> {
        return apiManager.savingAccountsListApi
            .getSavingsWithAssociations(accountId, Constants.TRANSACTIONS)
            .asResult().flowOn(dispatcher)
    }

    override suspend fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long,
    ): Flow<Result<TransactionsEntity>> {
        return apiManager.savingAccountsListApi
            .getSavingAccountTransaction(accountId, transactionId)
            .asResult().flowOn(dispatcher)
    }

    override suspend fun getSelfAccounts(clientId: Long): Flow<Result<ClientAccounts>> {
        return apiManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .asResult().flowOn(dispatcher)
    }

    override suspend fun getBeneficiaryList(): Flow<Result<List<Beneficiary>>> {
        return apiManager.beneficiaryApi.beneficiaryList().asResult().flowOn(dispatcher)
    }

    override suspend fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload): Flow<Result<CommonResponse>> {
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
