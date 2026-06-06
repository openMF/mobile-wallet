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

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mifospay.core.common.CurrencyFormatter

/**
 * ViewModel for the Payment Details screen
 * Manages payment details state and provides comprehensive transaction information
 * Currently uses placeholder data for demonstration purposes
 */
class PaymentDetailsViewModel : ViewModel() {

    private val _stateFlow = MutableStateFlow(createPlaceholderState())
    val stateFlow: StateFlow<PaymentDetailsState> = _stateFlow.asStateFlow()

    /**
     * Initializes the ViewModel with transaction ID
     * In a real app, this would fetch transaction details from the API
     */
    fun initialize(transactionId: String) {
        // TODO: Fetch transaction details from API using transactionId
        // For now, we'll use placeholder data
        _stateFlow.value = createPlaceholderState().copy(
            upiTransactionId = transactionId,
            mifosTransactionId = "MF$transactionId",
        )
    }

    /**
     * Creates placeholder data for demonstration purposes
     * Includes sample payment details with different scenarios
     */
    private fun createPlaceholderState(): PaymentDetailsState {
        return PaymentDetailsState(
            payeeName = "Jane Smith",
            isBusiness = false,
            bankingName = "TechCorp Solutions",
            phoneNumber = "+91 98765 43210",
            upiId = "jane.smith@upi",
            profileImageUrl = null,
            amount = "1,500.00",
            formattedAmount = "₹1,500.00",
            note = "Lunch payment",
            isPaymentSuccessful = true,
            upiTransactionId = "UPI123456789012345",
            payerName = "Biplab Dutta",
            payerBankName = "HDFC Bank",
            payerAccountLast4Digits = "1234",
            payerUpiAppName = "Google Pay",
            payerUpiId = "biplabdutta@okicici",
            upiAppName = "PhonePe",
            mifosTransactionId = "MF123456789",
            transactionDate = "27 Sept 2025, 2:30 PM",
        )
    }

    /**
     * Updates the payment status for demonstration purposes
     * In a real app, this would be called when payment status changes
     */
    fun updatePaymentStatus(isSuccessful: Boolean) {
        val currentState = _stateFlow.value
        _stateFlow.value = currentState.copy(
            isPaymentSuccessful = isSuccessful,
        )
    }

    /**
     * Updates the payment amount for demonstration purposes
     * In a real app, this would be called when amount changes
     */
    fun updateAmount(newAmount: String) {
        val currentState = _stateFlow.value
        val formattedAmount = try {
            val amountValue = newAmount.replace(",", "").toDoubleOrNull() ?: 0.0
            CurrencyFormatter.format(
                balance = amountValue,
                currencyCode = "INR",
                maximumFractionDigits = 2,
            )
        } catch (e: Exception) {
            "₹$newAmount"
        }

        _stateFlow.value = currentState.copy(
            amount = newAmount,
            formattedAmount = formattedAmount,
        )
    }
}
