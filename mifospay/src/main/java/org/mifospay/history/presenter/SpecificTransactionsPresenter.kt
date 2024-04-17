package org.mifospay.history.presenter

import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.TaskLooper.TaskData
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseFactory
import com.mifospay.core.model.domain.Transaction
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.R
import org.mifospay.base.BaseView
import org.mifospay.core.data.util.Constants
import org.mifospay.history.HistoryContract
import org.mifospay.history.HistoryContract.SpecificTransactionsView
import javax.inject.Inject

/**
 * Created by ankur on 08/June/2018
 */

class SpecificTransactionsPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mUseCaseFactory: UseCaseFactory
) :
    HistoryContract.SpecificTransactionsPresenter {
    private var mSpecificTransactionsView: SpecificTransactionsView? = null

    @JvmField
    @Inject
    var mTaskLooper: TaskLooper? = null

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
                    mTaskLooper?.addTask(
                        useCase = mUseCaseFactory.getUseCase(Constants.FETCH_ACCOUNT_TRANSFER_USECASE)
                                as UseCase<FetchAccountTransfer.RequestValues, FetchAccountTransfer.ResponseValue>,
                        values = FetchAccountTransfer.RequestValues(transferId!!),
                        taskData = TaskData(
                            org.mifospay.common.Constants.TRANSFER_DETAILS, i
                        )
                    )
                }
            }
            mTaskLooper!!.listen(object : TaskLooper.Listener {
                override fun <R : UseCase.ResponseValue?> onTaskSuccess(
                    taskData: TaskData, response: R
                ) {
                    when (taskData.taskName) {
                        org.mifospay.common.Constants.TRANSFER_DETAILS -> {
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

                override fun onFailure(message: String?) {
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