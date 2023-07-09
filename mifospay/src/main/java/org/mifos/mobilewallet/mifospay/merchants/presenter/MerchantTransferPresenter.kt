package org.mifos.mobilewallet.mifospay.merchants.presenter

import org.mifos.mobilewallet.core.base.TaskLooper
import org.mifos.mobilewallet.core.base.TaskLooper.TaskData
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseFactory
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionsHistoryAsync
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.MerchantTransferView
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * Created by Shivansh Tiwari on 06/07/19.
 */
class MerchantTransferPresenter @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val preferencesHelper: PreferencesHelper
) : BaseHomeContract.MerchantTransferPresenter,
    TransactionsHistoryAsync {
    @JvmField
    @Inject
    var transactionsHistory: TransactionsHistory? = null

    @JvmField
    @Inject
    var mTaskLooper: TaskLooper? = null

    @JvmField
    @Inject
    var mUseCaseFactory: UseCaseFactory? = null

    @JvmField
    @Inject
    var mFetchAccount: FetchAccount? = null
    private var mMerchantTransferView: MerchantTransferView? = null
    private var merchantAccountNumber: String? = null
    override fun attachView(baseView: BaseView<*>?) {
        mMerchantTransferView = baseView as MerchantTransferView?
        mMerchantTransferView!!.setPresenter(this)
        transactionsHistory!!.delegate = this
    }

    override fun checkBalanceAvailability(externalId: String?, transferAmount: Double) {
        mUsecaseHandler.execute(mFetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    mMerchantTransferView!!.hideSwipeProgress()
                    if (transferAmount > response.account.balance) {
                        mMerchantTransferView!!.showToast(Constants.INSUFFICIENT_BALANCE)
                    } else {
                        mMerchantTransferView!!.showPaymentDetails(externalId, transferAmount)
                    }
                }

                override fun onError(message: String) {
                    mMerchantTransferView!!.hideSwipeProgress()
                    mMerchantTransferView!!.showToast(Constants.ERROR_FETCHING_BALANCE)
                }
            })
    }

    override fun fetchMerchantTransfers(merchantAccountNumber: String?) {
        transactionsHistory!!.fetchTransactionsHistory(preferencesHelper.accountId)
        this.merchantAccountNumber = merchantAccountNumber
    }

    override fun onTransactionsFetchCompleted(transactions: List<Transaction>) {
        val specificTransactions = ArrayList<Transaction?>()
        if (transactions != null && transactions.size > 0) {
            for (i in transactions.indices) {
                val transaction = transactions[i]
                if (transaction.transferDetail == null
                    && transaction.transferId != 0L
                ) {
                    val transferId = transaction.transferId
                    mTaskLooper!!.addTask(
                        mUseCaseFactory!!.getUseCase(org.mifos.mobilewallet.core.utils.Constants.FETCH_ACCOUNT_TRANSFER_USECASE),
                        FetchAccountTransfer.RequestValues(transferId),
                        TaskData(Constants.TRANSFER_DETAILS, i)
                    )
                }
            }
            mTaskLooper!!.listen(object : TaskLooper.Listener {
                override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                    taskData: TaskData, response: R
                ) {
                    when (taskData.taskName) {
                        Constants.TRANSFER_DETAILS -> {
                            val responseValue = response as FetchAccountTransfer.ResponseValue
                            val index = taskData.taskId
                            transactions[index].transferDetail = responseValue.transferDetail
                        }
                    }
                }

                override fun onComplete() {
                    for (transaction in transactions) {
                        if (transaction.transferDetail != null && transaction.transferDetail.toAccount
                                .accountNo == merchantAccountNumber
                        ) {
                            specificTransactions.add(transaction)
                        }
                    }
                    if (specificTransactions.size == 0) {
                        showEmptyStateView()
                    } else {
                        //mMerchantTransferView.showToast("History Fetched Successfully");
                        mMerchantTransferView!!.showTransactions(specificTransactions)
                    }
                }

                override fun onFailure(message: String) {
                    showErrorStateView()
                }
            })
        }
    }

    private fun showErrorStateView() {
        mMerchantTransferView!!.showSpecificView(
            R.drawable.ic_error_state, R.string.error_oops,
            R.string.error_no_transaction_history_subtitle
        )
    }

    private fun showEmptyStateView() {
        mMerchantTransferView!!.showSpecificView(
            R.drawable.ic_history,
            R.string.empty_no_transaction_history_title,
            R.string.empty_no_transaction_history_subtitle
        )
    }
}