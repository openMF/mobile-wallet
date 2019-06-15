package org.mifos.mobilewallet.mifospay.paymenthub

import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionStatus
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

interface TransactionContract {

    interface TransactionPresenter: BasePresenter {

        fun createQRData(qrData: QRData)

        fun decodeQRData(qrString: String)

        fun createTransaction(transaction: Transaction)

        fun fetchTransactionInfo(transactionId: String)

        fun updateEndPoints(fspName : String, headerTenant : String)
    }

    interface TransactionView: BaseView<TransactionPresenter> {

        fun showQR(qrString: String)

        fun qrDecoded(qrData: QRData)

        fun transactionCreated(transactionInfo: TransactionInfo)

        fun showTransactionStatus(transactionStatus: TransactionStatus)

        fun showTransactionError(message: String)
    }
}