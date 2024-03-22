package org.mifos.mobilewallet.mifospay.feature.auth.mobile_verify

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient
import javax.inject.Inject

@HiltViewModel
class MobileVerificationViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val searchClientUseCase: SearchClient
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MobileVerificationUiState>(MobileVerificationUiState.VerifyPhone)
    val uiState: StateFlow<MobileVerificationUiState> = _uiState

    var showProgress by mutableStateOf(false)

    /**
     * Verify Mobile number that it already exist or not then request otp
     */
    fun verifyMobileAndRequestOtp(
        fullNumber: String, mobileNo: String,
        onError: (String?) -> Unit
    ) {
        showProgress = true
        mUseCaseHandler.execute(searchClientUseCase,
            fullNumber.let { SearchClient.RequestValues(it) },
            object : UseCase.UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    onError("Mobile number already exists.")
                    showProgress = false
                }

                override fun onError(message: String) {
                    requestOtp(fullNumber)
                }
            })
    }

    /**
     * Request Otp from server
     */
    fun requestOtp(fullNumber: String) {
        viewModelScope.launch {
            delay(2000)
            showProgress = false
            _uiState.update {
                MobileVerificationUiState.VerifyOtp
            }
        }
    }

    /**
     * Verify Otp
     */
    fun verifyOTP(otp: String?, onOtpVerifySuccess: () -> Unit) {
        showProgress = true
        viewModelScope.launch {
            delay(2000)
            showProgress = false
            onOtpVerifySuccess()
        }
    }
}

sealed interface MobileVerificationUiState {
    data object VerifyOtp : MobileVerificationUiState
    data object VerifyPhone : MobileVerificationUiState
}
