package com.mifos.passcode.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.passcode.data.SetBiometricPublicKeyRepository
import com.mifos.passcode.data.VerifyBiometric
import com.mifos.passcode.utility.AuthenticationResult
import com.mifos.passcode.utility.BioMetricUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BiometricAuthorizationViewModel: ViewModel() {
    private val setBiometricPublicKeyRepository =
        SetBiometricPublicKeyRepository()
    private val verifyBiometric = VerifyBiometric()

    private val _state: MutableStateFlow<BiometricState> =
        MutableStateFlow(BiometricState(false, null))
    private val _effect: MutableSharedFlow<BiometricEffect> = MutableSharedFlow(replay = 0)

    val state: StateFlow<BiometricState>
        get() = _state

    val effect: SharedFlow<BiometricEffect>
        get() = _effect

    fun setBiometricAuthorization(bioMetricUtil: BioMetricUtil) {
        viewModelScope.launch {
            _state.value = BiometricState(isLoading = true, error = null)
            if (!bioMetricUtil.canAuthenticate()) {
                _state.value = BiometricState(isLoading = true, error = "Biometric not available")
                return@launch
            }
            val publicKey = bioMetricUtil.setAndReturnPublicKey() ?: ""
            setBiometricPublicKeyRepository.set(publicKey)
            _state.value = BiometricState(isLoading = false, error = null)
            _effect.emit(BiometricEffect.BiometricSetSuccess)

        }
    }

    fun authorizeBiometric(bioMetricUtil: BioMetricUtil) {
        viewModelScope.launch {
            when(val biometricResult = bioMetricUtil.authenticate()) {
                AuthenticationResult.AttemptExhausted -> {
                    _state.value = BiometricState(isLoading = false, error = "Attempt Exhausted")
                }
                is AuthenticationResult.Error -> {
                    _state.value = BiometricState(isLoading = false, error = biometricResult.error)
                }
                AuthenticationResult.Failed -> {
                    _state.value = BiometricState(isLoading = false, error = "Biometric Failed")
                }
                AuthenticationResult.NegativeButtonClick -> {
                    _state.value = BiometricState(isLoading = false, error = "Biometric Canceled")
                }
                AuthenticationResult.Success -> {
                    _state.value = BiometricState(isLoading = true, error = null)
                    val signedUserId = bioMetricUtil.signUserId("userId")
                    val result = verifyBiometric.verify(signedUserId)
                    if (result.isSuccess) {
                        _state.value = BiometricState(isLoading = false, error = null)
                        _effect.emit(BiometricEffect.BiometricAuthSuccess)
                    } else {
                        _state.value = BiometricState(isLoading = false, error = result.exceptionOrNull()!!.message)
                    }
                }
            }

        }
    }
}

data class BiometricState(
    val isLoading: Boolean,
    val error: String?
)

sealed class BiometricEffect {
    data object BiometricSetSuccess: BiometricEffect()
    data object BiometricAuthSuccess: BiometricEffect()
}