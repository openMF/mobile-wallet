package org.mifos.mobilewallet.mifospay.history

import com.mifos.mobilewallet.model.entity.accounts.savings.TransferDetail
import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by naman on 17/8/17.
 */
interface HistoryContract {
    interface TransactionsHistoryAsync {
        fun onTransactionsFetchCompleted(transactions: List<Transaction>?)
    }

    interface HistoryView : BaseView<TransactionsHistoryPresenter?> {
        fun showRecyclerView()
        fun showStateView(drawable: Int, title: Int, subtitle: Int)
        fun showTransactions(transactions: List<Transaction>?)
        fun showEmptyTransactionTypeStateView(drawable: Int, title: String?, subtitle: String?)
        fun showTransactionDetailDialog(transactionIndex: Int, accountNumber: String?)
        fun showHistoryFetchingProgress()
        fun refreshTransactions(transactions: List<Transaction>?)
    }

    interface TransactionsHistoryPresenter : BasePresenter {
        fun fetchTransactions()
        fun filterTransactionType(type: TransactionType?)
        fun handleTransactionClick(transactionIndex: Int)
    }

    interface TransactionDetailView : BaseView<TransactionDetailPresenter?> {
        fun showTransferDetail(transferDetail: TransferDetail?)
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String?)
    }

    interface TransactionDetailPresenter : BasePresenter {
        fun getTransferDetail(transferId: Long)
    }

    interface SpecificTransactionsView : BaseView<SpecificTransactionsPresenter?> {
        fun showSpecificTransactions(specificTransactions: ArrayList<Transaction?>)
        fun showProgress()
        fun hideProgress()
        fun showStateView(drawable: Int, title: Int, subtitle: Int)
    }

    interface SpecificTransactionsPresenter : BasePresenter {
        fun getSpecificTransactions(
            transactions: ArrayList<Transaction?>?,
            secondAccountNumber: String?
        ): ArrayList<Transaction?>?
    }
}