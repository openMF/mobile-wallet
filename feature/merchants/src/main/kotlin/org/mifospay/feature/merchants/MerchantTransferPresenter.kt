package org.mifospay.feature.merchants

import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.TaskLooper.TaskData
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseFactory
import com.mifospay.core.model.domain.Transaction
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.data.util.Constants.FETCH_ACCOUNT_TRANSFER_USECASE
import org.mifospay.R
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.history.HistoryContract.TransactionsHistoryAsync
import org.mifospay.history.TransactionsHistory
import org.mifospay.home.BaseHomeContract
import org.mifospay.home.BaseHomeContract.MerchantTransferView
import org.mifospay.common.Constants
import javax.inject.Inject

/**
 * Created by Shivansh Tiwari on 06/07/19.
 */
class MerchantTransferPresenter @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val preferencesHelper: PreferencesHelper,
    private val transactionsHistory: TransactionsHistory,
    private val mUseCaseFactory: UseCaseFactory,
    private val mFetchAccount: FetchAccount
) : BaseHomeContract.MerchantTransferPresenter, TransactionsHistoryAsync {

    @Inject
    lateinit var mTaskLooper: TaskLooper

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

    override fun onTransactionsFetchCompleted(transactions: List<Transaction>?) {
        val specificTransactions = ArrayList<Transaction?>()
        if (!transactions.isNullOrEmpty()) {
            for (i in transactions.indices) {
                val transaction = transactions[i]
                if (transaction.transferDetail == null
                    && transaction.transferId != 0L
                ) {
                    val transferId = transaction.transferId
                    mTaskLooper.addTask(
                        useCase = mUseCaseFactory.getUseCase(FETCH_ACCOUNT_TRANSFER_USECASE)
                                as UseCase<FetchAccountTransfer.RequestValues, FetchAccountTransfer.ResponseValue>,
                        values = transferId.let { FetchAccountTransfer.RequestValues(it) },
                        taskData = TaskData(Constants.TRANSFER_DETAILS, i)
                    )
                }
            }
            mTaskLooper.listen(object : TaskLooper.Listener {
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

                override fun onFailure(message: String?) {
                    showErrorStateView()
                }
            })
        }
    }
}