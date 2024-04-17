package org.mifospay.history

import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseFactory
import com.mifospay.core.model.domain.Transaction
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransactions
import org.mifospay.history.HistoryContract.TransactionsHistoryAsync
import javax.inject.Inject

class TransactionsHistory @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val fetchAccountTransactionsUseCase: FetchAccountTransactions,
    private val mFetchAccountUseCase: FetchAccount
) {
    var delegate: TransactionsHistoryAsync? = null

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
        mUsecaseHandler.execute(fetchAccountTransactionsUseCase,
            FetchAccountTransactions.RequestValues(accountId),
            object : UseCaseCallback<FetchAccountTransactions.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransactions.ResponseValue?) {
                    transactions = response?.transactions
                    delegate!!.onTransactionsFetchCompleted(transactions)
                }

                override fun onError(message: String) {
                    transactions = null
                }
            })
    }
}