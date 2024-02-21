package org.mifos.mobilewallet.mifospay.history.presenter

import org.mifos.mobilewallet.core.base.TaskLooper
import org.mifos.mobilewallet.core.base.TaskLooper.TaskData
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseFactory
import org.mifos.mobilewallet.core.base.UseCaseHandler
import com.mifos.mobilewallet.model.domain.Transaction
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer
import org.mifos.mobilewallet.core.utils.Constants
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.history.HistoryContract
import org.mifos.mobilewallet.mifospay.history.HistoryContract.SpecificTransactionsView
import javax.inject.Inject

/**
 * Created by ankur on 08/June/2018
 */

class SpecificTransactionsPresenter @Inject constructor(private val mUseCaseHandler: UseCaseHandler) :
    HistoryContract.SpecificTransactionsPresenter {
    private var mSpecificTransactionsView: SpecificTransactionsView? = null

    @JvmField
    @Inject
    var mTaskLooper: TaskLooper? = null

    @JvmField
    @Inject
    var mUseCaseFactory: UseCaseFactory? = null
    override fun getSpecificTransactions(
        transactions: ArrayList<Transaction?>?,
        secondAccountNumber: String?
    ): ArrayList<Transaction?> {
        val specificTransactions = ArrayList<Transaction?>()
        mSpecificTransactionsView!!.showProgress()
        if (transactions?.size!! > 0) {
            for (i in transactions.indices) {
                val transaction = transactions[i]
                if (transaction?.transferDetail == null
                    && transaction?.transferId != 0L
                ) {
                    val transferId = transaction?.transferId
                    mTaskLooper!!.addTask(
                        mUseCaseFactory
                            ?.getUseCase(Constants.FETCH_ACCOUNT_TRANSFER_USECASE),
                        transferId?.let { FetchAccountTransfer.RequestValues(it) },
                        TaskData(
                            org.mifos.mobilewallet.mifospay.utils.Constants.TRANSFER_DETAILS,
                            i
                        )
                    )
                }
            }
            mTaskLooper!!.listen(object : TaskLooper.Listener {
                override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                    taskData: TaskData, response: R
                ) {
                    when (taskData.taskName) {
                        org.mifos.mobilewallet.mifospay.utils.Constants.TRANSFER_DETAILS -> {
                            val responseValue = response as FetchAccountTransfer.ResponseValue
                            val index = taskData.taskId
                            transactions[index]?.transferDetail = responseValue.transferDetail
                        }
                    }
                }

                override fun onComplete() {
                    for (transaction in transactions) {
                        if (transaction?.transferDetail != null
                            && (transaction.transferDetail.fromAccount
                                .accountNo ==
                                    secondAccountNumber || transaction.transferDetail?.toAccount
                                ?.accountNo == secondAccountNumber)
                        ) {
                            specificTransactions.add(transaction)
                        }
                    }
                    mSpecificTransactionsView!!.showSpecificTransactions(specificTransactions)
                }

                override fun onFailure(message: String) {
                    mSpecificTransactionsView!!.showStateView(
                        R.drawable.ic_error_state,
                        R.string.error_oops,
                        R.string.error_specific_transactions
                    )
                }
            })
        }
        return specificTransactions
    }

    override fun attachView(baseView: BaseView<*>?) {
        mSpecificTransactionsView = baseView as SpecificTransactionsView?
        mSpecificTransactionsView!!.setPresenter(this)
    }

}