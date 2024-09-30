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
import org.mifospay.core.network.model.CommonResponse

interface SelfServiceRepository {
    suspend fun loginSelf(payload: AuthenticationPayload): Flow<Result<User>>

    suspend fun getSelfClientDetails(clientId: Long): Flow<Result<Client>>

    suspend fun getSelfClientDetails(): Flow<Result<Page<Client>>>

    suspend fun getSelfAccountTransactions(
        accountId: Long,
    ): Flow<Result<SavingsWithAssociationsEntity>>

    suspend fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long,
    ): Flow<Result<TransactionsEntity>>

    suspend fun getSelfAccounts(clientId: Long): Flow<Result<ClientAccounts>>

    suspend fun getBeneficiaryList(): Flow<Result<List<Beneficiary>>>

    suspend fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload): Flow<Result<CommonResponse>>

    suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): Flow<Result<CommonResponse>>
}
