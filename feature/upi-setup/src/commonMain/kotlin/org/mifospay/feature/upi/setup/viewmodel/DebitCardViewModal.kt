/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.upi.setup.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mifospay.core.ui.utils.BaseViewModel

class DebitCardViewModel :
    BaseViewModel<DebitCardUiState, DebitCardEvent, DebitCardAction>(
        initialState = DebitCardUiState.Initials,
    ) {

    val debitCardUiState: StateFlow<DebitCardUiState> get() = stateFlow

    override fun handleAction(action: DebitCardAction) {
        when (action) {
            is DebitCardAction.VerifyDebitCard -> {
                val otp = "0000"
                viewModelScope.launch {
                    mutableStateFlow.value = DebitCardUiState.Verifying
                    delay(2000)

                    val isVerified = verifyDebitCardNumber(action.debitCardNumber)
                    if (isVerified) {
                        mutableStateFlow.value = DebitCardUiState.Verified(otp)
                    } else {
                        mutableStateFlow.value = DebitCardUiState.VerificationFailed(
                            "Invalid Debit Card Number",
                        )
                    }
                }
            }
        }
    }

    fun verifyDebitCard(debitCardNumber: String, month: String, year: String) {
        trySendAction(DebitCardAction.VerifyDebitCard(debitCardNumber, month, year))
    }

    private fun verifyDebitCardNumber(debitCardNumber: String): Boolean {
        return debitCardNumber.length in 12..19
    }
}

sealed interface DebitCardEvent

sealed interface DebitCardAction {
    data class VerifyDebitCard(
        val debitCardNumber: String,
        val month: String,
        val year: String,
    ) : DebitCardAction
}

sealed class DebitCardUiState {
    data object Initials : DebitCardUiState()
    data object Verifying : DebitCardUiState()
    data class Verified(val otp: String) : DebitCardUiState()
    data class VerificationFailed(val errorMessage: String) : DebitCardUiState()
}
