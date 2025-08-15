/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.update
import org.mifospay.core.data.util.StandardUpiQrCodeProcessor
import org.mifospay.core.ui.utils.BaseViewModel

class PayeeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PayeeDetailsState, PayeeDetailsEvent, PayeeDetailsAction>(
    initialState = PayeeDetailsState(),
) {

    init {
        val safeQrCodeDataString = savedStateHandle.get<String>("qrCodeData") ?: ""

        if (safeQrCodeDataString.isNotEmpty()) {
            // URL decode the QR code data to restore special characters
            val qrCodeDataString = safeQrCodeDataString.urlDecode()
            val isUpiCode = StandardUpiQrCodeProcessor.isValidUpiQrCode(qrCodeDataString)

            val qrCodeData = if (isUpiCode) {
                StandardUpiQrCodeProcessor.parseUpiQrCode(qrCodeDataString)
            } else {
                StandardUpiQrCodeProcessor.parseUpiQrCode("upi://pay?pa=$qrCodeDataString&pn=Unknown")
            }

            mutableStateFlow.update {
                it.copy(
                    payeeName = qrCodeData.payeeName,
                    upiId = qrCodeData.payeeVpa,
                    phoneNumber = "",
                    amount = qrCodeData.amount,
                    note = qrCodeData.transactionNote,
                    isAmountEditable = qrCodeData.amount.isEmpty(),
                    isUpiCode = true,
                )
            }
        }
    }

    override fun handleAction(action: PayeeDetailsAction) {
        when (action) {
            is PayeeDetailsAction.NavigateBack -> {
                sendEvent(PayeeDetailsEvent.NavigateBack)
            }
            is PayeeDetailsAction.UpdateAmount -> {
                val cleanAmount = action.amount.replace(",", "")
                val isValidAmount = cleanAmount.isEmpty() || cleanAmount.toDoubleOrNull() != null

                if (isValidAmount) {
                    val amountValue = cleanAmount.toDoubleOrNull() ?: 0.0
                    val showMessage = amountValue > 500000

                    mutableStateFlow.value = stateFlow.value.copy(
                        amount = cleanAmount,
                        showMaxAmountMessage = showMessage,
                    )
                }
            }
            is PayeeDetailsAction.UpdateNote -> {
                mutableStateFlow.value = stateFlow.value.copy(note = action.note)
            }
            is PayeeDetailsAction.ProceedToPayment -> {
                val currentState = stateFlow.value
                if (currentState.isUpiCode) {
                    sendEvent(PayeeDetailsEvent.NavigateToUpiPayment(currentState))
                } else {
                    sendEvent(PayeeDetailsEvent.NavigateToFineractPayment(currentState))
                }
            }
        }
    }
}

data class PayeeDetailsState(
    val payeeName: String = "",
    val upiId: String = "",
    val phoneNumber: String = "",
    val amount: String = "",
    val note: String = "",
    val isAmountEditable: Boolean = true,
    val isUpiCode: Boolean = false,
    val isLoading: Boolean = false,
    val showMaxAmountMessage: Boolean = false,
) {
    val formattedAmount: String
        get() = if (amount.isEmpty()) "0" else formatAmountWithCommas(amount)

    val isAmountExceedingMax: Boolean
        get() = amount.toDoubleOrNull()?.let { it > 500000 } ?: false

    private fun formatAmountWithCommas(amountStr: String): String {
        val cleanAmount = amountStr.replace(",", "")
        return try {
            val amount = cleanAmount.toDouble()
            if (amount == 0.0) return "0"

            val parts = amount.toString().split(".")
            val integerPart = parts[0]
            val decimalPart = if (parts.size > 1) parts[1] else ""

            val formattedInteger = integerPart.reversed()
                .chunked(3)
                .joinToString(",")
                .reversed()

            if (decimalPart.isNotEmpty()) {
                "$formattedInteger.$decimalPart"
            } else {
                formattedInteger
            }
        } catch (e: NumberFormatException) {
            amountStr
        }
    }
}

sealed interface PayeeDetailsEvent {
    data object NavigateBack : PayeeDetailsEvent
    data class NavigateToUpiPayment(val state: PayeeDetailsState) : PayeeDetailsEvent
    data class NavigateToFineractPayment(val state: PayeeDetailsState) : PayeeDetailsEvent
}

sealed interface PayeeDetailsAction {
    data object NavigateBack : PayeeDetailsAction
    data class UpdateAmount(val amount: String) : PayeeDetailsAction
    data class UpdateNote(val note: String) : PayeeDetailsAction
    data object ProceedToPayment : PayeeDetailsAction
}

/**
 * URL decodes a string to restore special characters from navigation
 *
 * Optimized for UPI QR codes with future-proofing for common special characters.
 *
 * Essential UPI characters (12):
 * - URL structure: ?, &, =, %
 * - VPA format: @
 * - Common text: space, ", ', comma
 * - URLs: /, :, #, +
 *
 * Future-proofing characters (5):
 * - Currency symbols: $
 * - URL parameters: ;
 * - JSON/structured data: [, ], {, }
 *
 * Note: %25 (percent) must be decoded last to avoid double decoding.
 */
private fun String.urlDecode(): String {
    return this.replace("%20", " ")
        .replace("%26", "&")
        .replace("%3D", "=")
        .replace("%3F", "?")
        .replace("%40", "@")
        .replace("%2B", "+")
        .replace("%2F", "/")
        .replace("%3A", ":")
        .replace("%23", "#")
        .replace("%22", "\"")
        .replace("%27", "'")
        .replace("%2C", ",")
        .replace("%24", "$")
        .replace("%3B", ";")
        .replace("%5B", "[")
        .replace("%5D", "]")
        .replace("%7B", "{")
        .replace("%7D", "}")
        .replace("%25", "%")
}
