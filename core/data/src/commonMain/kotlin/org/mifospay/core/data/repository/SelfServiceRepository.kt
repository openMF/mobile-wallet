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
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.authentication.AuthenticationPayload
import org.mifospay.core.network.model.entity.beneficary.Beneficiary
import org.mifospay.core.network.model.entity.beneficary.BeneficiaryPayload
import org.mifospay.core.network.model.entity.beneficary.BeneficiaryUpdatePayload
import org.mifospay.core.network.model.entity.user.User

interface SelfServiceRepository {
    suspend fun loginSelf(payload: AuthenticationPayload): Result<User>

    suspend fun getSelfClientDetails(clientId: Long): Result<Client>

    suspend fun getSelfClientDetails(): Flow<Result<Page<Client>>>

    fun getSelfAccountTransactions(
        accountId: Long,
    ): Flow<List<Transaction>>

    suspend fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long,
    ): Result<Flow<Transaction>>

    suspend fun getSelfAccounts(clientId: Long): Result<List<Account>>

    suspend fun getBeneficiaryList(): Flow<Result<List<Beneficiary>>>

    suspend fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload): Flow<Result<CommonResponse>>

    suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): Flow<Result<CommonResponse>>
}
