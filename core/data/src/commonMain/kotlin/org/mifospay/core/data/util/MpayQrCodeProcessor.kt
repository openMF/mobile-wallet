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

import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.model.utils.QrCodeType
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * MPay QR Code Encoder and Decoder with validation.
 *
 * MPay string format:
 * ```
 * mpay://pay?qt=0&ci=123&am=100&cn=John&an=ACC001&ai=456&cu=USD&oi=1&pi=2&pn=&mode=02&s=000000
 * ```
 *
 * Type values (qt):
 * - qt=0 → INTRA_BANK
 * - qt=1 → INTER_BANK
 * - qt=2 → BENEFICIARY
 * - qt=3 → MERCHANT
 */
@OptIn(ExperimentalEncodingApi::class)
object MpayQrCodeProcessor {
    // Regex patterns for validation
    private val CURRENCY_PATTERN = Regex("^[A-Z]{3}$")

    // Maximum allowed amount
    private const val MAX_AMOUNT = 100000.0

    // Protocol constants
    private const val MPAY_PROTOCOL = "mpay://pay"
    private const val UPI_PROTOCOL = "upi://pay" // For backwards compatibility

    /**
     * Encodes MPay payment details into a Base64 encoded string.
     *
     * Supports multiple QR formats based on [QrCodeData.type]:
     * - INTRA_BANK: Contains internal Mifos IDs (clientId, accountId > 0)
     * - INTER_BANK: Contains only phone number (clientId, accountId = 0, phoneNumber set)
     * - BENEFICIARY: For adding beneficiaries
     * - MERCHANT: For merchant payments
     *
     * @param qrCodeData MPay payment request details
     * @return Base64 encoded MPay payment string
     * @throws IllegalArgumentException for invalid input
     */
    fun encodeMpayString(qrCodeData: QrCodeData): String {
        // Validate input data
        validate(qrCodeData)

        // Build MPay string with type
        val requestPaymentString = buildString {
            append(MPAY_PROTOCOL)
            append("?qt=${qrCodeData.type.ordinal}")
            append("&ci=${qrCodeData.clientId}")
            append("&am=${qrCodeData.amount}")
            append("&cn=${qrCodeData.clientName}")
            append("&an=${qrCodeData.accountNo}")
            append("&ai=${qrCodeData.accountId}")
            append("&cu=${qrCodeData.currency}")
            append("&oi=${qrCodeData.officeId}")
            append("&pi=${qrCodeData.accountTypeId}")
            append("&pn=${qrCodeData.phoneNumber ?: ""}")
            append("&mode=02")
            append("&s=000000")
        }

        return requestPaymentString.encodeBase64()
    }

    /**
     * Decodes a Base64 encoded MPay or UPI payment string.
     *
     * Supports both new mpay:// and legacy upi:// formats for backwards compatibility.
     *
     * @param encodedString Base64 encoded payment string
     * @return Decoded QrCodeData object
     * @throws IllegalArgumentException for invalid encoded string
     */
    fun decodeMpayString(encodedString: String): QrCodeData {
        // Decode the Base64 string
        val decodedString = encodedString.decodeBase64String()

        // Parse based on protocol (support both mpay:// and upi:// for backwards compat)
        val params = when {
            decodedString.startsWith(MPAY_PROTOCOL) -> parseMpayString(decodedString, MPAY_PROTOCOL)
            decodedString.startsWith(UPI_PROTOCOL) -> parseMpayString(decodedString, UPI_PROTOCOL)
            else -> throw IllegalArgumentException("Invalid QR code format: unknown protocol")
        }

        // Get phoneNumber for inter-bank QR support
        val phoneNumber = params["pn"]?.takeIf { it.isNotBlank() }

        // For inter-bank QR codes (phone number only), clientId and accountId may be 0
        val clientId = params["ci"]?.toLongOrNull() ?: 0L
        val accountId = params["ai"]?.toLongOrNull() ?: 0L

        // Determine QR type - check qt field first, then infer from fields
        val type = params["qt"]?.toIntOrNull()?.let { QrCodeType.fromOrdinal(it) }
            ?: inferTypeFromFields(clientId, accountId, phoneNumber)

        // Validate: must have either internal IDs or phone number
        val hasInternalIds = clientId > 0 && accountId > 0
        val hasPhoneNumber = phoneNumber != null

        if (!hasInternalIds && !hasPhoneNumber) {
            throw IllegalArgumentException("QR code missing required data: needs internal IDs or phone number")
        }

        // Create QrCodeData
        val requestQrData = QrCodeData(
            type = type,
            clientId = clientId,
            clientName = params["cn"] ?: "",
            accountNo = params["an"] ?: "",
            amount = params["am"] ?: "",
            accountId = accountId,
            currency = params["cu"] ?: QrCodeData.DEFAULT_CURRENCY,
            officeId = params["oi"]?.toLongOrNull() ?: QrCodeData.OFFICE_ID,
            accountTypeId = params["pi"]?.toLongOrNull() ?: QrCodeData.ACCOUNT_TYPE_ID,
            phoneNumber = phoneNumber,
        )

        // Validate the created object (only for intra-bank with internal IDs)
        if (hasInternalIds) {
            validate(requestQrData)
        }

        return requestQrData
    }

    /**
     * Infers the QR type from fields when qt parameter is not present (legacy QR codes).
     */
    private fun inferTypeFromFields(
        clientId: Long,
        accountId: Long,
        phoneNumber: String?,
    ): QrCodeType {
        return when {
            clientId > 0 && accountId > 0 -> QrCodeType.INTRA_BANK
            phoneNumber != null -> QrCodeType.INTER_BANK
            else -> QrCodeType.INTRA_BANK
        }
    }

    /**
     * Validates the QrCodeData
     * @param data MPay payment request details to validate
     * @throws IllegalArgumentException for any validation failures
     */
    private fun validate(data: QrCodeData) {
        // Name validation
        require(data.clientName.isNotBlank()) {
            "Client name cannot be empty"
        }

        require(data.clientName.length <= 50) {
            "Client name too long (max 50 characters)"
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
                "Amount exceeds maximum limit of $MAX_AMOUNT"
            }
        }

        // Currency validation
        require(CURRENCY_PATTERN.matches(data.currency)) {
            "Invalid currency code. Must be 3 uppercase letters"
        }
    }

    /**
     * Parses the MPay/UPI payment string into a map of parameters
     * @param paymentString Decoded payment string
     * @param protocol The protocol prefix to strip (mpay://pay or upi://pay)
     * @return Map of parameter keys and values
     */
    private fun parseMpayString(paymentString: String, protocol: String): Map<String, String> {
        return paymentString
            .substringAfter("$protocol?")
            .split("&")
            .associate {
                val (key, value) = it.split("=")
                key to value
            }
    }

    /**
     * Backwards-compatible method for legacy code.
     */
    @Deprecated(
        message = "Use encodeMpayString instead",
        replaceWith = ReplaceWith("encodeMpayString(qrCodeData)"),
    )
    fun encodeUpiString(qrCodeData: QrCodeData): String = encodeMpayString(qrCodeData)

    /**
     * Backwards-compatible method for legacy code.
     */
    @Deprecated(
        message = "Use decodeMpayString instead",
        replaceWith = ReplaceWith("decodeMpayString(encodedString)"),
    )
    fun decodeUpiString(encodedString: String): QrCodeData = decodeMpayString(encodedString)
}

/**
 * Type alias for backwards compatibility with code that still uses UpiQrCodeProcessor.
 */
@Deprecated(
    message = "Use MpayQrCodeProcessor instead",
    replaceWith = ReplaceWith("MpayQrCodeProcessor"),
)
typealias UpiQrCodeProcessor = MpayQrCodeProcessor
