package org.mifospay.feature.upi_setup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebitCardViewModel @Inject constructor() : ViewModel() {

    private val _debitCardUiState = MutableStateFlow<DebitCardUiState>(DebitCardUiState.Initials)
    val debitCardUiState: StateFlow<DebitCardUiState> = _debitCardUiState

    fun verifyDebitCard(debitCardNumber: String, month: String, year: String) {
        val otp = "0000"
        viewModelScope.launch {
            _debitCardUiState.value = DebitCardUiState.Verifying
            delay(2000) // Simulating a verification delay
            val isVerified = verifyDebitCardNumber(debitCardNumber, month, year)
            if (isVerified) {
                _debitCardUiState.value = DebitCardUiState.Verified(otp)
            } else {
                _debitCardUiState.value = DebitCardUiState.VerificationFailed(
                    "Invalid Debit Card Number"
                )

            }
        }
    }

    private fun verifyDebitCardNumber(
        debitCardNumber: String, month: String, year: String
    ): Boolean {
        return debitCardNumber.length in 12..19
    }

}

sealed class DebitCardUiState {
    data object Initials : DebitCardUiState()
    data object Verifying : DebitCardUiState()
    data class Verified(val otp: String) : DebitCardUiState()
    data class VerificationFailed(val errorMessage: String) : DebitCardUiState()
}