package org.mifos.mobilewallet.mifospay.paymenthub.presenter

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.CreateTransaction
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.EncodeQR
import org.mifos.mobilewallet.core.domain.usecase.paymenthub.FetchTransactionInfo
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.paymenthub.TransactionContract
import javax.inject.Inject

class TransactionPresenter @Inject constructor(val useCaseHandler: UseCaseHandler): TransactionContract.TransactionPresenter {

    private lateinit var transactionView: TransactionContract.TransactionView

    @set:Inject
    lateinit var encodeQR: EncodeQR

    @set:Inject
    lateinit var createTransaction: CreateTransaction

    @set:Inject
    lateinit var fetchTransactionInfo: FetchTransactionInfo

    override fun attachView(baseView: BaseView<*>?) {
        transactionView = baseView as TransactionContract.TransactionView
    }

    override fun createQRData(qrData: QRData) {
        useCaseHandler.execute(encodeQR, EncodeQR.RequestValues(qrData),
                object : UseCase.UseCaseCallback<EncodeQR.ResponseValue> {
                    override fun onSuccess(response: EncodeQR.ResponseValue?) {
                        response?.let {
                            transactionView.showQR(it.qrString)
                        } ?: kotlin.run {
                            onError("Unable to encode qr")
                        }
                    }
                    override fun onError(message: String?) {

                    }
                } )
    }

}