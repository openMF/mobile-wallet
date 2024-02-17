package org.mifos.mobilewallet.mifospay.receipt

import okhttp3.ResponseBody
import com.mifos.mobilewallet.model.entity.accounts.savings.TransferDetail
import com.mifos.mobilewallet.model.domain.Transaction
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 06/June/2018
 */
interface ReceiptContract {
    interface ReceiptView : BaseView<ReceiptPresenter?> {
        fun showSnackbar(message: String?)
        fun writeReceiptToPDF(responseBody: ResponseBody?, filename: String?)
        fun hideProgressDialog()
        fun showTransactionDetail(transaction: Transaction?)
        fun showTransferDetail(transferDetail: TransferDetail?)
        fun openPassCodeActivity()
    }

    interface ReceiptPresenter : BasePresenter {
        fun downloadReceipt(transactionId: String?)
        fun fetchTransaction(transactionId: Long)
    }
}