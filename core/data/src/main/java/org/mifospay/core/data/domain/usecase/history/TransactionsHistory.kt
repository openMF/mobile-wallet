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
import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseFactory
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransactions
import javax.inject.Inject

class TransactionsHistory @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val fetchAccountTransactionsUseCase: FetchAccountTransactions,
) {
    var delegate: HistoryContract.TransactionsHistoryAsync? = null

    @JvmField
    @Inject
    var mTaskLooper: TaskLooper? = null

    @JvmField
    @Inject
    var mUseCaseFactory: UseCaseFactory? = null
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
