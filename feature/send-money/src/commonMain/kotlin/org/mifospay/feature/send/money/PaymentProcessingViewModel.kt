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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.ui.utils.BaseViewModel

class PaymentProcessingViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PaymentProcessingState, PaymentProcessingEvent, PaymentProcessingAction>(
    initialState = PaymentProcessingState(),
) {

    init {
        val payeeName = savedStateHandle.get<String>("payeeName") ?: ""
        val amount = savedStateHandle.get<String>("amount") ?: ""
        val isUpiCode = savedStateHandle.get<Boolean>("isUpiCode") ?: false

        mutableStateFlow.update {
            it.copy(
                payeeName = payeeName,
                amount = amount,
                isUpiCode = isUpiCode,
            )
        }

        startPaymentProcessing()
    }

    private fun startPaymentProcessing() {
        viewModelScope.launch {
            try {
                mutableStateFlow.update { it.copy(isProcessing = true) }

                delay(3000) // Simulate payment processing time

                mutableStateFlow.update { it.copy(isProcessing = false) }

                delay(1000) // Show completion state briefly

                sendEvent(PaymentProcessingEvent.PaymentComplete)
            } catch (e: Exception) {
                sendEvent(PaymentProcessingEvent.PaymentFailed(e.message ?: "Payment failed"))
            }
        }
    }

    override fun handleAction(action: PaymentProcessingAction) {
        when (action) {
            PaymentProcessingAction.RetryPayment -> {
                startPaymentProcessing()
            }
        }
    }
}

data class PaymentProcessingState(
    val payeeName: String = "",
    val amount: String = "",
    val isUpiCode: Boolean = false,
    val isProcessing: Boolean = true,
) {
    val formattedAmount: String
        get() = if (amount.isEmpty()) "₹0" else "₹$amount"
}

sealed interface PaymentProcessingEvent {
    data object PaymentComplete : PaymentProcessingEvent
    data class PaymentFailed(val errorMessage: String) : PaymentProcessingEvent
}

sealed interface PaymentProcessingAction {
    data object RetryPayment : PaymentProcessingAction
}
