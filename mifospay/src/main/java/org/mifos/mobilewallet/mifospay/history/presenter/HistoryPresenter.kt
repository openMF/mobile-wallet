package org.mifos.mobilewallet.mifospay.history.presenter

import org.mifos.mobilewallet.core.base.TaskLooper
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseFactory
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.model.Account
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.model.TransactionType
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.history.HistoryContract.HistoryView
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionsHistoryAsync
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionsHistoryPresenter
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory
import java.util.Locale
import javax.inject.Inject

class HistoryPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : TransactionsHistoryPresenter, TransactionsHistoryAsync {
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
            object : UseCaseCallback<FetchAccount.ResponseValue?> {
                override fun onSuccess(response: FetchAccount.ResponseValue?) {
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

    override fun filterTransactionType(type: TransactionType?) {
        val filterTransactions: MutableList<Transaction> = ArrayList()
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