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
import org.mifospay.core.model.account.AccountTransferPayload
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.model.search.AccountResult

interface AccountRepository {
    fun getTransaction(accountId: Long, transactionId: Long): Flow<DataState<Transaction>>

    fun getAccountTransfer(transferId: Long): Flow<DataState<TransferDetail>>

    fun searchAccounts(query: String): Flow<DataState<List<AccountResult>>>

    fun getSelfAccounts(clientId: Long): Flow<DataState<List<Account>>>

    suspend fun makeTransfer(payload: AccountTransferPayload): DataState<String>
}
