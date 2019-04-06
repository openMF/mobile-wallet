package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import java.net.URL
import java.net.URLDecoder
import javax.inject.Inject

class DecodeQR @Inject constructor() : UseCase<DecodeQR.RequestValues, DecodeQR.ResponseValue>() {

    private val qrSchema = "upi://pay?"

    override fun executeUseCase(requestValues: RequestValues) {
        val qrString = requestValues.qrString
        val qrDataArray = qrString.replace(qrSchema, "").split("&")

        val qrData = QRData()
        for (s in qrDataArray) {
            val data = s.split("=")
            when (data.get(0)) {
                "pa" -> {
                    qrData.idType = data.get(1).split("::")[0]
                    qrData.id = data.get(1).split("::")[1]
                }
                "pn" -> qrData.name = URLDecoder.decode(data.get(1), "UTF-8")
                "mc" -> qrData.code = data.get(1)
                "tr" -> qrData.clientRefId = URLDecoder.decode(data.get(1), "UTF-8")
                "tn" -> qrData.note = URLDecoder.decode(data.get(1), "UTF-8")
                "am" -> qrData.amount = data.get(1)
                "cu" -> qrData.currency = data.get(1)
                "refUrl" -> qrData.refUrl = URLDecoder.decode(data.get(1), "UTF-8")
            }
        }

        useCaseCallback.onSuccess(ResponseValue(qrData))

    }

    class RequestValues(val qrString: String) : UseCase.RequestValues

    class ResponseValue(val qrData: QRData) : UseCase.ResponseValue
}
