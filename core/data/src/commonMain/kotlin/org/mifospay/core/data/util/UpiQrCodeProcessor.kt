/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.util

import io.ktor.utils.io.core.toByteArray
import org.mifospay.core.model.utils.PaymentQrData
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * UPI QR Code Encoder and Decoder with validation
 */
@OptIn(ExperimentalEncodingApi::class)
object UpiQrCodeProcessor {
    // Regex patterns for validation
    private val VPA_PATTERN = Regex("^[a-zA-Z0-9.-]+@[a-zA-Z0-9]+$")
    private val CURRENCY_PATTERN = Regex("^[A-Z]{3}$")

    // Maximum allowed amount
    private const val MAX_AMOUNT = 100000.0

    /**
     * Encodes UPI payment details into a Base64 encoded string
     * @param paymentQrData UPI payment request details
     * @return Base64 encoded UPI payment string
     * @throws IllegalArgumentException for invalid input
     */
    fun encodeUpiString(paymentQrData: PaymentQrData): String {
        // Validate input data
        validate(paymentQrData)

        // Build UPI string
        val requestPaymentString = buildString {
            append("upi://pay")
            append("?pa=${paymentQrData.vpaId}")

            // Add amount if not empty
            if (paymentQrData.amount.isNotEmpty()) {
                append("&am=${paymentQrData.amount}")
            }

            append("&pn=${paymentQrData.name}")
            append("&ac=${paymentQrData.accountNo}")
            append("&cu=${paymentQrData.currency}")
            append("&mode=02")
            append("&s=000000")
        }

        return Base64.encode(requestPaymentString.toByteArray())
    }

    /**
     * Decodes a Base64 encoded UPI payment string
     * @param encodedString Base64 encoded UPI payment string
     * @return Decoded RequestQrData object
     * @throws IllegalArgumentException for invalid encoded string
     */
    fun decodeUpiString(encodedString: String): PaymentQrData {
        // Decode the Base64 string
        val decodedString = try {
            Base64.decode(encodedString).toString()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Base64 encoded string")
        }

        // Extract parameters
        val params = parseUpiString(decodedString)

        // Create RequestQrData
        val requestQrData = PaymentQrData(
            vpaId = params["pa"] ?: throw IllegalArgumentException("Missing VPA"),
            name = params["pn"] ?: throw IllegalArgumentException("Missing payee name"),
            accountNo = params["ac"] ?: throw IllegalArgumentException("Missing account number"),
            currency = params["cu"] ?: throw IllegalArgumentException("Missing currency"),
            amount = params["am"] ?: "",
        )

        // Validate the created object
        validate(requestQrData)

        return requestQrData
    }

    /**
     * Validates the RequestQrData
     * @param data UPI payment request details to validate
     * @throws IllegalArgumentException for any validation failures
     */
    private fun validate(data: PaymentQrData) {
        // VPA validation
//        require(VPA_PATTERN.matches(data.vpaId)) {
//            "Invalid VPA format. Must be in username@provider format"
//        }

        // Name validation
        require(data.name.isNotBlank()) {
            "Payee name cannot be empty"
        }
        require(data.name.length <= 50) {
            "Payee name too long (max 50 characters)"
        }

        // Account number validation
        require(data.accountNo.isNotBlank()) {
            "Account number cannot be empty"
        }

        // Optional amount validation (if not empty)
        if (data.amount.isNotEmpty()) {
            val amountValue = data.amount.toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid amount format")

            require(amountValue > 0) {
                "Amount must be positive"
            }
            require(amountValue <= MAX_AMOUNT) {
                "Amount exceeds maximum limit of ₹$MAX_AMOUNT"
            }
        }

        // Currency validation
        require(CURRENCY_PATTERN.matches(data.currency)) {
            "Invalid currency code. Must be 3 uppercase letters"
        }
    }

    /**
     * Parses the UPI payment string into a map of parameters
     * @param upiString Decoded UPI payment string
     * @return Map of parameter keys and values
     */
    private fun parseUpiString(upiString: String): Map<String, String> {
        return upiString
            .substringAfter("upi://pay?")
            .split("&")
            .associate {
                val (key, value) = it.split("=")
                key to value
            }
    }
}
