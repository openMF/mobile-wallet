package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

class EncodeQR @Inject constructor() : UseCase<EncodeQR.RequestValues, EncodeQR.ResponseValue>() {

    private val qrSchema = "upi://pay?"

    override fun executeUseCase(requestValues: RequestValues) {
        val qrData = requestValues.qrData

        val qrString = qrSchema + "pa=" + qrData.idType + "::" + qrData.id +
                "&pn=" + URLEncoder.encode(qrData.name).toString() + "&mc="+ qrData.code + "&tr=" + URLEncoder.encode(qrData.clientRefId) +
                "&tn=" + URLEncoder.encode(qrData.note) + "&am=" + qrData.amount + "&cu=" + qrData.currency +
                "&refUrl=" + URLEncoder.encode(qrData.refUrl)

        useCaseCallback.onSuccess(ResponseValue(qrString))

    }

    class RequestValues(val qrData: QRData) : UseCase.RequestValues

    class ResponseValue(val qrString: String) : UseCase.ResponseValue
}
