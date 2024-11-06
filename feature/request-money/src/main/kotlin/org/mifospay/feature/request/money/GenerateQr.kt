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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.ByteMatrix
import com.google.zxing.qrcode.encoder.Encoder
import com.google.zxing.qrcode.encoder.QRCode
import org.mifospay.core.data.base.UseCase
import java.util.Base64
import java.util.EnumMap

// Taken reference from this article
// https://ihareshvaghela.medium.com/generating-dotted-qr-codes-in-android-using-zxing-library-b02e824c895c

class GenerateQr(private val context: Context) : UseCase<
    GenerateQr.RequestValues,
    GenerateQr
        .ResponseValue?,
    >() {
    override fun executeUseCase(requestValues: RequestValues) {
        try {
            val bitmap = encodeAsBitmap(makeUpiString(requestValues.data), getLogoBitmap())
            useCaseCallback.onSuccess(ResponseValue(bitmap))
        } catch (e: WriterException) {
            useCaseCallback.onError("Failed to write data to QR")
        }
    }

    private fun makeUpiString(requestQrData: RequestQrData): String {
        val requestPaymentString = "upi://pay" +
            "?pa=${requestQrData.vpaId}" +
            "&am=${requestQrData.amount}" +
            "&pn=${requestQrData.name}" +
            "&cu=${requestQrData.currency}" +
            "&mode=02" +
            "&s=000000"
        val sign =
            Base64.getEncoder().encodeToString(requestPaymentString.toByteArray(Charsets.UTF_8))
        return "$requestPaymentString&sign=$sign"
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(str: String, logo: Bitmap?): Bitmap {
        val encodingHints: MutableMap<EncodeHintType, Any> =
            EnumMap(com.google.zxing.EncodeHintType::class.java)
        encodingHints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val code: QRCode = Encoder.encode(str, ErrorCorrectionLevel.H, encodingHints)
        return renderQRImage(code, logo)
    }

    // Function to render QR code
    private fun renderQRImage(code: QRCode, logo: Bitmap?): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = BLUE
        }

        val input = code.matrix
        val inputWidth = input.width
        val inputHeight = input.height
        val qrWidth = inputWidth + (QUIET_ZONE * 2)
        val qrHeight = inputHeight + (QUIET_ZONE * 2)
        val outputWidth = WIDTH.coerceAtLeast(qrWidth)
        val outputHeight = HEIGHT.coerceAtLeast(qrHeight)
        val multiple = (outputWidth / qrWidth).coerceAtMost(outputHeight / qrHeight)
        val leftPadding = (outputWidth - (inputWidth * multiple)) / 2
        val topPadding = (outputHeight - (inputHeight * multiple)) / 2

        drawQrCodeDots(
            input,
            canvas,
            paint,
            leftPadding,
            topPadding,
            inputWidth,
            inputHeight,
            multiple,
        )

        val circleDiameter = multiple * FINDER_PATTERN_SIZE
        drawFinderPatternSquareStyle(canvas, paint, leftPadding, topPadding, circleDiameter)
        drawFinderPatternSquareStyle(
            canvas,
            paint,
            leftPadding + (inputWidth - FINDER_PATTERN_SIZE) * multiple,
            topPadding,
            circleDiameter,
        )
        drawFinderPatternSquareStyle(
            canvas,
            paint,
            leftPadding,
            topPadding + (inputHeight - FINDER_PATTERN_SIZE) * multiple,
            circleDiameter,
        )

        logo?.let {
            drawLogo(canvas, bitmap, it)
        }

        return bitmap
    }

    private fun drawQrCodeDots(
        input: ByteMatrix,
        canvas: Canvas,
        paint: Paint,
        leftPadding: Int,
        topPadding: Int,
        inputWidth: Int,
        inputHeight: Int,
        multiple: Int,
    ) {
        val circleSize = (multiple * CIRCLE_SCALE_DOWN_FACTOR).toInt()
        for (inputY in 0 until inputHeight) {
            var outputY = topPadding
            outputY += multiple * inputY
            for (inputX in 0 until inputWidth) {
                var outputX = leftPadding
                outputX += multiple * inputX
                if (input.get(inputX, inputY).toInt() == 1 &&
                    !isFinderPattern(inputX, inputY, inputWidth, inputHeight)
                ) {
                    canvas.drawCircle(
                        (outputX + multiple / 2).toFloat(),
                        (outputY + multiple / 2).toFloat(),
                        circleSize.toFloat() / 2f,
                        paint,
                    )
                }
            }
        }
    }

    private fun isFinderPattern(
        inputX: Int,
        inputY: Int,
        inputWidth: Int,
        inputHeight: Int,
    ): Boolean {
        return (
            inputX <= FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                inputX >= inputWidth - FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                inputX <= FINDER_PATTERN_SIZE && inputY >= inputHeight - FINDER_PATTERN_SIZE
            )
    }

    private fun drawLogo(canvas: Canvas, bitmap: Bitmap, logo: Bitmap) {
        val logoSize = (WIDTH * 0.11).toInt()
        val centerX = (bitmap.width - logoSize) / 2
        val centerY = (bitmap.height - logoSize) / 2
        val backgroundRadius = (logoSize * 0.75).toFloat()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ELLIPSE_COLOR
        }

        // Draw logo background
        canvas.drawCircle(
            (centerX + logoSize / 2).toFloat(),
            (centerY + logoSize / 2).toFloat(),
            backgroundRadius + 10f,
            paint,
        )

        paint.color = Color.WHITE
        canvas.drawCircle(
            (centerX + logoSize / 2).toFloat(),
            (centerY + logoSize / 2).toFloat(),
            backgroundRadius + 6f,
            paint,
        )

        // Draw the logo
        val logoRect = Rect(0, 0, logo.width, logo.height)
        val destRect = Rect(centerX, centerY, centerX + logoSize, centerY + logoSize)
        canvas.drawBitmap(logo, logoRect, destRect, null)
    }

    // Function to draw a finder pattern
    private fun drawFinderPatternSquareStyle(
        canvas: Canvas,
        paint: Paint,
        x: Int,
        y: Int,
        squareDiameter: Int,
    ) {
        val outerRadius = squareDiameter * 0.25f
        val middleRadius = squareDiameter * 0.15f
        val innerRadius = squareDiameter * 0.1f
        val middleSquareScale = 0.7f
        val innerSquareScale = 0.45f

        paint.color = PATTERN_COLOR
        canvas.drawRoundRect(
            RectF(
                x.toFloat(),
                y.toFloat(),
                (x + squareDiameter).toFloat(),
                (y + squareDiameter).toFloat(),
            ),
            outerRadius,
            outerRadius,
            paint,
        )

        val middleSquareSize = squareDiameter * middleSquareScale
        val middleSquareOffset = (squareDiameter - middleSquareSize) / 2
        paint.color = Color.WHITE
        canvas.drawRoundRect(
            RectF(
                (x + middleSquareOffset),
                (y + middleSquareOffset),
                (x + middleSquareOffset + middleSquareSize),
                (y + middleSquareOffset + middleSquareSize),
            ),
            middleRadius,
            middleRadius,
            paint,
        )

        val innerSquareSize = squareDiameter * innerSquareScale
        val innerSquareOffset = (squareDiameter - innerSquareSize) / 2
        paint.color = PATTERN_COLOR
        canvas.drawRoundRect(
            RectF(
                (x + innerSquareOffset),
                (y + innerSquareOffset),
                (x + innerSquareOffset + innerSquareSize),
                (y + innerSquareOffset + innerSquareSize),
            ),
            innerRadius,
            innerRadius,
            paint,
        )
    }

    // Function to get logo in the center of QR code
    private fun getLogoBitmap(): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, R.drawable.logo)
        return drawable?.let {
            val bitmap =
                Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            bitmap
        }
    }

    data class RequestValues(val data: RequestQrData) : UseCase.RequestValues
    data class ResponseValue(val bitmap: Bitmap) : UseCase.ResponseValue
    companion object {
        private const val BLUE = 0xFF0673BA.toInt() // dots color
        private const val PATTERN_COLOR = 0xFF6e6e6e.toInt() // corner square color
        private const val ELLIPSE_COLOR = 0xFFe9e9e9.toInt() // logo background ellipse color
        private const val WIDTH = 500 // width of QR code
        private const val HEIGHT = 500 // height of QR code
        private const val FINDER_PATTERN_SIZE = 13 // pattern size (in this case corner squares)
        private const val CIRCLE_SCALE_DOWN_FACTOR = 1f // size of dots in qr code
        private const val QUIET_ZONE = 5 // spacing from all sides for QR code
    }
}
