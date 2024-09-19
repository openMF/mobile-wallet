/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.read.qr.utils

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.mifospay.core.data.base.UseCase

class ScanQr  : UseCase<ScanQr.RequestValues, ScanQr.ResponseValue?>() {

    override fun executeUseCase(requestValues: RequestValues) {
        val bitmap = requestValues.bitmap
        try {
            val result = decodeQrCode(bitmap)
            if (result != null) {
                useCaseCallback.onSuccess(ResponseValue(result.text))
            } else {
                useCaseCallback.onError("Failed to decode QR code")
            }
        } catch (e: Exception) {
            useCaseCallback.onError("Error decoding QR code: ${e.message}")
        }
    }

    private fun decodeQrCode(bitmap: Bitmap): Result? {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()

        return reader.decode(binaryBitmap)
    }

    class RequestValues(val bitmap: Bitmap) : UseCase.RequestValues
    class ResponseValue(val result: String) : UseCase.ResponseValue
}
