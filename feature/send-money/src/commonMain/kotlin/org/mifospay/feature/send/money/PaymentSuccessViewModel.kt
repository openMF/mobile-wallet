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
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.mifospay.core.common.DateHelper
import org.mifospay.core.ui.utils.BaseViewModel

class PaymentSuccessViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PaymentSuccessState, PaymentSuccessEvent, PaymentSuccessAction>(
    initialState = PaymentSuccessState(),
) {

    init {
        val payeeName = savedStateHandle.get<String>("payeeName") ?: ""
        val amount = savedStateHandle.get<String>("amount") ?: ""
        val upiName = savedStateHandle.get<String>("upiName") ?: payeeName
        val transactionTimestamp = savedStateHandle.get<String>("transactionTimestamp") // Unix timestamp from PSP or mobile device
        val timestamp = if (transactionTimestamp != null) {
            formatTransactionTimestamp(transactionTimestamp)
        } else {
            getCurrentTimestamp()
        }

        mutableStateFlow.update {
            it.copy(
                payeeName = payeeName,
                amount = amount,
                upiName = upiName,
                timestamp = timestamp,
            )
        }
    }

    override fun handleAction(action: PaymentSuccessAction) {
        when (action) {
            PaymentSuccessAction.ShareScreenshot -> {
                sendEvent(PaymentSuccessEvent.ShareScreenshot)
            }
            PaymentSuccessAction.Done -> {
                sendEvent(PaymentSuccessEvent.NavigateToHome)
            }
        }
    }

    /**
     * Formats Unix timestamp from PSP or mobile device into readable format
     * Falls back to current device timestamp if parsing fails
     */
    private fun formatTransactionTimestamp(unixTimestamp: String): String {
        return try {
            val timestamp = unixTimestamp.toLong()
            val instant = Instant.fromEpochSeconds(timestamp)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

            val day = localDateTime.dayOfMonth
            val month = localDateTime.month.name.lowercase().capitalize()
            val year = localDateTime.year
            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')
            val amPm = if (localDateTime.hour < 12) "am" else "pm"

            "$day $month $year, $hour:$minute $amPm"
        } catch (e: Exception) {
            // Fallback to current timestamp if parsing fails
            getCurrentTimestamp()
        }
    }

    private fun getCurrentTimestamp(): String {
        val currentDateTime = DateHelper.currentDate
        val day = currentDateTime.dayOfMonth
        val month = currentDateTime.month.name.lowercase().capitalize()
        val year = currentDateTime.year
        val hour = currentDateTime.hour.toString().padStart(2, '0')
        val minute = currentDateTime.minute.toString().padStart(2, '0')
        val amPm = if (currentDateTime.hour < 12) "am" else "pm"

        return "$day $month $year, $hour:$minute $amPm"
    }
}

data class PaymentSuccessState(
    val payeeName: String = "",
    val amount: String = "",
    val upiName: String = "",
    val timestamp: String = "",
) {
    val formattedAmount: String
        get() = if (amount.isEmpty()) {
            "₹0.00"
        } else {
            if (amount.contains(".") && amount.split(".")[1].length == 2) {
                "₹$amount"
            } else {
                "₹$amount.00"
            }
        }
}

sealed interface PaymentSuccessEvent {
    data object ShareScreenshot : PaymentSuccessEvent
    data object NavigateToHome : PaymentSuccessEvent
}

sealed interface PaymentSuccessAction {
    data object ShareScreenshot : PaymentSuccessAction
    data object Done : PaymentSuccessAction
}
