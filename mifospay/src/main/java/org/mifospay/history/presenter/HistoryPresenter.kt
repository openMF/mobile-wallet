package org.mifospay.history.presenter

import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseFactory
import com.mifospay.core.model.domain.Account
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransactions
import org.mifospay.R
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.history.HistoryContract.HistoryView
import org.mifospay.history.HistoryContract.TransactionsHistoryAsync
import org.mifospay.history.HistoryContract.TransactionsHistoryPresenter
import org.mifospay.history.TransactionsHistory
import java.util.Locale
import javax.inject.Inject

class HistoryPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val mFetchAccountUseCase: FetchAccount,
    private val fetchAccountTransactionsUseCase: FetchAccountTransactions
) : TransactionsHistoryPresenter, TransactionsHistoryAsync {

    @JvmField
    @Inject
    var mTaskLooper: TaskLooper? = null

    @JvmField
    @Inject
    var mUseCaseFactory: UseCaseFactory? = null

    @JvmField
    @Inject
    var mTransactionsHistory: TransactionsHistory? = null
    private var mHistoryView: HistoryView? = null
    private var mAccount: Account? = null
    private var allTransactions: List<Transaction>? = null
    override fun attachView(baseView: BaseView<*>?) {
        mHistoryView = baseView as HistoryView?
        mHistoryView!!.setPresenter(this)
        mTransactionsHistory!!.delegate = this
    }

    override fun fetchTransactions() {
        mHistoryView!!.showHistoryFetchingProgress()
        mUseCaseHandler.execute(mFetchAccountUseCase,
            FetchAccount.RequestValues(mLocalRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    mAccount = response?.account
                    response?.account?.id?.let {
                        mTransactionsHistory
                            ?.fetchTransactionsHistory(it)
                    }
                }

                override fun onError(message: String) {
                    showErrorStateView()
                }
            })
    }

    override fun filterTransactionType(type: TransactionType) {
        val filterTransactions: MutableList<Transaction> = ArrayList()

        if (type == TransactionType.OTHER) {
            if (allTransactions!!.isNotEmpty()) {
                mHistoryView!!.refreshTransactions(allTransactions)
            } else {
                showEmptyStateView()
            }
        } else {
            for (transaction in allTransactions!!) {
                if (transaction.transactionType == type) {
                    filterTransactions.add(transaction)
                }
            }
            if (filterTransactions.isEmpty()) {
                showEmptyTransactionTypeStateView(type.toString().lowercase(Locale.getDefault()))
            } else {
                mHistoryView!!.refreshTransactions(filterTransactions)
            }
        }

    }

    override fun handleTransactionClick(transactionIndex: Int) {
        val accountNumber = if (mAccount != null) mAccount!!.number else ""
        mHistoryView!!.showTransactionDetailDialog(transactionIndex, accountNumber)
    }

    private fun showErrorStateView() {
        mHistoryView!!.showStateView(
            R.drawable.ic_error_state, R.string.error_oops,
            R.string.error_no_transaction_history_subtitle
        )
    }

    private fun showEmptyStateView() {
        mHistoryView!!.showStateView(
            R.drawable.ic_history,
            R.string.empty_no_transaction_history_title,
            R.string.empty_no_transaction_history_subtitle
        )
    }

    private fun showEmptyTransactionTypeStateView(type: String) {
        mHistoryView!!.showEmptyTransactionTypeStateView(
            R.drawable.ic_history,
            "You have no $type transactions",
            "Every $type transaction will be displaying here"
        )
    }

    override fun onTransactionsFetchCompleted(transactions: List<Transaction>?) {
        if (transactions == null) {
            showErrorStateView()
        } else {
            val transactionsAmount = transactions.size
            if (transactionsAmount > 0) {
                allTransactions = transactions
                mHistoryView!!.showTransactions(transactions)
            } else {
                showEmptyStateView()
            }
        }
    }
}