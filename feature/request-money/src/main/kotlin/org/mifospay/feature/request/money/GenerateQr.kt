/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import org.mifospay.common.Constants
import org.mifospay.core.data.base.UseCase
import java.util.Base64
import javax.inject.Inject

/**
 * Created by naman on 8/7/17.
 */
class GenerateQr @Inject constructor() :
    UseCase<GenerateQr.RequestValues, GenerateQr.ResponseValue?>() {
    override fun executeUseCase(requestValues: RequestValues) {
        try {
            val bitmap = encodeAsBitmap(makeUpiString(requestValues.data))
            if (bitmap != null) {
                useCaseCallback.onSuccess(ResponseValue(bitmap))
            } else {
                useCaseCallback.onError(Constants.ERROR_OCCURRED)
            }
        } catch (e: WriterException) {
            useCaseCallback.onError(Constants.FAILED_TO_WRITE_DATA_TO_QR)
        }
    }

    private fun makeUpiString(requestQrData: RequestQrData): String {
        // Initial payment string
        val requestPaymentString = "upi://pay" +
            "?pa=${requestQrData.vpaId}" +
            "&am=${requestQrData.amount}" +
            "&pn=${requestQrData.name}" +
            "&cu=${requestQrData.currency}" +
            "&mode=02" +
            "&s=000000"

        // Convert the payment string to bytes and encode to Base64
        val sign =
            Base64.getEncoder().encodeToString(requestPaymentString.toByteArray(Charsets.UTF_8))

        val signedRequestPayment = requestPaymentString +
            "&sign=$sign"

        // Convert the final URI to string
        return signedRequestPayment
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(str: String): Bitmap? {
        val result: BitMatrix = try {
            MultiFormatWriter().encode(
                str,
                BarcodeFormat.QR_CODE,
                WIDTH,
                WIDTH,
                null,
            )
        } catch (iae: IllegalArgumentException) {
            return null
        }
        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h)
        return bitmap
    }

    class RequestValues(val data: RequestQrData) : UseCase.RequestValues
    class ResponseValue(val bitmap: Bitmap) : UseCase.ResponseValue
    companion object {
        private const val WHITE = -0x1
        private const val BLACK = -0x1000000
        private const val WIDTH = 500
    }
}
