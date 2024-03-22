package org.mifos.mobilewallet.mifospay.history.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.history.HistoryContract
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionDetailView
import org.mifos.mobilewallet.mifospay.common.Constants
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