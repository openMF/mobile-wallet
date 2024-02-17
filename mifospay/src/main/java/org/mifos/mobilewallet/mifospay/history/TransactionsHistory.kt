package org.mifos.mobilewallet.mifospay.history

import org.mifos.mobilewallet.core.base.TaskLooper
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseFactory
import org.mifos.mobilewallet.core.base.UseCaseHandler
import com.mifos.mobilewallet.model.domain.Transaction
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionsHistoryAsync
import javax.inject.Inject

class TransactionsHistory @Inject constructor(private val mUsecaseHandler: UseCaseHandler) {
    var delegate: TransactionsHistoryAsync? = null

    @JvmField
    @Inject
    var mFetchAccountUseCase: FetchAccount? = null

    @JvmField
    @Inject
    var fetchAccountTransactionsUseCase: FetchAccountTransactions? = null

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