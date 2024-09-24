/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.history

import com.mifospay.core.model.domain.Transaction
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransactions

class TransactionsHistory(
    private val mUseCaseHandler: UseCaseHandler,
    private val fetchAccountTransactionsUseCase: FetchAccountTransactions,
) {
    var delegate: HistoryContract.TransactionsHistoryAsync? = null
    private var transactions: List<Transaction>?

    init {
        transactions = ArrayList()
    }

    fun fetchTransactionsHistory(accountId: Long) {
        mUseCaseHandler.execute(
            fetchAccountTransactionsUseCase,
            FetchAccountTransactions.RequestValues(accountId),
            object : UseCaseCallback<FetchAccountTransactions.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransactions.ResponseValue?) {
                    transactions = response?.transactions
                    delegate!!.onTransactionsFetchCompleted(transactions)
                }

                override fun onError(message: String) {
                    transactions = null
                }
            },
        )
    }
}
