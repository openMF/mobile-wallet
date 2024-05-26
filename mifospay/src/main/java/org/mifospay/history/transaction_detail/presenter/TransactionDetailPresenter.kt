package org.mifospay.history.transaction_detail.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.base.BaseView
import org.mifospay.history.HistoryContract
import org.mifospay.history.HistoryContract.TransactionDetailView
import org.mifospay.common.Constants
import javax.inject.Inject

/**
 * Created by ankur on 06/June/2018
 */
class TransactionDetailPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mFetchAccountTransferUseCase: FetchAccountTransfer
) : HistoryContract.TransactionDetailPresenter {

    private var mTransactionDetailView: TransactionDetailView? = null

    override fun attachView(baseView: BaseView<*>?) {
        mTransactionDetailView = baseView as TransactionDetailView?
        mTransactionDetailView!!.setPresenter(this)
    }

    override fun getTransferDetail(transferId: Long) {
        mTransactionDetailView!!.showProgressBar()
        mUseCaseHandler.execute(mFetchAccountTransferUseCase,
            FetchAccountTransfer.RequestValues(transferId),
            object : UseCaseCallback<FetchAccountTransfer.ResponseValue?> {

                override fun onSuccess(response: FetchAccountTransfer.ResponseValue?) {
                    mTransactionDetailView!!.hideProgressBar()
                    mTransactionDetailView!!.showTransferDetail(response?.transferDetail)
                }

                override fun onError(message: String) {
                    mTransactionDetailView!!.hideProgressBar()
                    mTransactionDetailView!!.showToast(
                        Constants.ERROR_FETCHING_TRANSACTION_DETAILS
                    )
                }
            })
    }
}