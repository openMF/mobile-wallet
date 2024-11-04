/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.upi.setup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DebitCardViewModel : ViewModel() {

    private val _debitCardUiState = MutableStateFlow<DebitCardUiState>(DebitCardUiState.Initials)
    val debitCardUiState: StateFlow<DebitCardUiState> = _debitCardUiState

    @Suppress("UnusedParameter")
    fun verifyDebitCard(debitCardNumber: String, month: String, year: String) {
        val otp = "0000"
        viewModelScope.launch {
            _debitCardUiState.value = DebitCardUiState.Verifying
            delay(2000)

            val isVerified = verifyDebitCardNumber(debitCardNumber)
            if (isVerified) {
                _debitCardUiState.value = DebitCardUiState.Verified(otp)
            } else {
                _debitCardUiState.value = DebitCardUiState.VerificationFailed(
                    "Invalid Debit Card Number",
                )
            }
        }
    }

    private fun verifyDebitCardNumber(debitCardNumber: String): Boolean {
        return debitCardNumber.length in 12..19
    }
}

sealed class DebitCardUiState {
    data object Initials : DebitCardUiState()
    data object Verifying : DebitCardUiState()
    data class Verified(val otp: String) : DebitCardUiState()
    data class VerificationFailed(val errorMessage: String) : DebitCardUiState()
}
