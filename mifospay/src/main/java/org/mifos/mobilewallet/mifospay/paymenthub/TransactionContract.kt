package org.mifos.mobilewallet.mifospay.paymenthub

import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

interface TransactionContract {

    interface TransactionPresenter: BasePresenter {

        fun createQRData(qrData: QRData)
    }

    interface TransactionView: BaseView<TransactionPresenter> {

        fun showQR(qrString: String)
    }
}