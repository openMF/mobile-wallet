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
import org.mifospay.core.common.DataState
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.AccountContent
import org.mifospay.core.model.account.AccountsWithTransactions
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.authentication.AuthenticationPayload
import org.mifospay.core.network.model.entity.user.User

interface SelfServiceRepository {
    suspend fun loginSelf(payload: AuthenticationPayload): DataState<User>

    fun getSelfClientDetails(clientId: Long): Flow<DataState<Client>>

    suspend fun getSelfClientDetails(): Flow<DataState<Page<Client>>>

    fun getSelfAccountTransactions(
        accountId: Long,
    ): Flow<List<Transaction>>

    suspend fun getSelfAccountTransactionFromId(
        accountId: Long,
        transactionId: Long,
    ): DataState<Flow<Transaction>>

    fun getSelfAccounts(clientId: Long): Flow<DataState<List<Account>>>

    fun getBeneficiaryList(): Flow<DataState<List<Beneficiary>>>

    fun getActiveAccountsWithTransactions(
        clientId: Long,
        limit: Int,
    ): Flow<DataState<AccountsWithTransactions>>

    fun getAccountsTransactions(clientId: Long): Flow<DataState<List<Transaction>>>

    fun getTransactions(accountId: List<Long>, limit: Int?): Flow<List<Transaction>>

    fun getAccountAndBeneficiaryList(clientId: Long): Flow<DataState<AccountContent>>

    suspend fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload): DataState<String>

    suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): DataState<String>

    suspend fun deleteBeneficiary(beneficiaryId: Long): DataState<String>
}
