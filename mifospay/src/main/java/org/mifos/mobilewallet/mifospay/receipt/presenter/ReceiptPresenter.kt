package org.mifos.mobilewallet.mifospay.receipt.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.DownloadTransactionReceipt
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransaction
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.receipt.ReceiptContract
import org.mifos.mobilewallet.mifospay.receipt.ReceiptContract.ReceiptView
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * Created by ankur on 06/June/2018
 */
class ReceiptPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val preferencesHelper: PreferencesHelper
) : ReceiptContract.ReceiptPresenter {
    private lateinit var mDownloadTransactionReceiptUseCase: DownloadTransactionReceipt
    private lateinit var  mFetchAccountTransfer: FetchAccountTransfer
    private lateinit var  mFetchAccountTransaction: FetchAccountTransaction
    private lateinit var mReceiptView: ReceiptView
    override fun attachView(baseView: BaseView<*>?) {
        mReceiptView = baseView as ReceiptView
        mReceiptView.setPresenter(this)
    }

    override fun downloadReceipt(transactionId: String?) {
        mUseCaseHandler.execute(mDownloadTransactionReceiptUseCase,
            DownloadTransactionReceipt.RequestValues(transactionId),
            object : UseCaseCallback<DownloadTransactionReceipt.ResponseValue> {
                override fun onSuccess(response: DownloadTransactionReceipt.ResponseValue) {
                    mReceiptView.writeReceiptToPDF(
                        response.responseBody,
                        Constants.RECEIPT + transactionId + Constants.PDF
                    )
                }

                override fun onError(message: String) {
                    mReceiptView.showSnackbar(Constants.ERROR_FETCHING_RECEIPT)
                }
            })
    }

    override fun fetchTransaction(transactionId: Long) {
        val accountId = preferencesHelper.accountId
        mUseCaseHandler.execute(mFetchAccountTransaction,
            FetchAccountTransaction.RequestValues(accountId, transactionId),
            object : UseCaseCallback<FetchAccountTransaction.ResponseValue> {
                override fun onSuccess(response: FetchAccountTransaction.ResponseValue) {
                    mReceiptView.showTransactionDetail(response.transaction)
                    fetchTransfer(response.transaction.transferId)
                }

                override fun onError(message: String) {
                    if (message == Constants.UNAUTHORIZED_ERROR) {
                        mReceiptView.openPassCodeActivity()
                    } else {
                        mReceiptView.hideProgressDialog()
                        mReceiptView.showSnackbar("Error fetching Transaction")
                    }
                }
            }
        )
    }

    fun fetchTransfer(transferId: Long) {
        mUseCaseHandler.execute(mFetchAccountTransfer,
            FetchAccountTransfer.RequestValues(transferId),
            object : UseCaseCallback<FetchAccountTransfer.ResponseValue> {
                override fun onSuccess(response: FetchAccountTransfer.ResponseValue) {
                    mReceiptView.showTransferDetail(response.transferDetail)
                }

                override fun onError(message: String) {
                    mReceiptView.hideProgressDialog()
                    mReceiptView.showSnackbar("Error fetching Account Transfer")
                }
            })
    }
}