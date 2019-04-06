package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData

class EncodeQR : UseCase<EncodeQR.RequestValues, EncodeQR.ResponseValue>() {

    private val qrSchema = "upi://pay?"

    override fun executeUseCase(requestValues: RequestValues) {
        val qrData = requestValues.qrData

        val qrString = qrSchema + "pa=" + qrData.idType + "::" + qrData.id +
                "&pn=" + qrData.name + "&mc="+ qrData.code + "&tr=" + qrData.clientRefId +
                "&tn=" + qrData.note + "&am=" + qrData.amount + "&cu=" + qrData.currency +
                "&refUrl=" + qrData.refUrl

        useCaseCallback.onSuccess(ResponseValue(qrString))

    }

    class RequestValues(val qrData: QRData) : UseCase.RequestValues

    class ResponseValue(val qrString: String) : UseCase.ResponseValue
}
