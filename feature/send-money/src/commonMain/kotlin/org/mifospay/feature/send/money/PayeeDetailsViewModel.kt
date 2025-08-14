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
            // Restore & characters that were replaced for safe navigation
            val qrCodeDataString = safeQrCodeDataString.replace("___AMP___", "&")
            val qrCodeData = if (StandardUpiQrCodeProcessor.isValidUpiQrCode(qrCodeDataString)) {
                StandardUpiQrCodeProcessor.parseUpiQrCode(qrCodeDataString)
            } else {
                // For non-UPI QR codes, create a basic StandardUpiQrData
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
                mutableStateFlow.value = stateFlow.value.copy(amount = action.amount)
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
)

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
