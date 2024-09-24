/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.mobileVerify

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.SearchClient

@Suppress("UnusedParameter")
class MobileVerificationViewModel(
    private val mUseCaseHandler: UseCaseHandler,
    private val searchClientUseCase: SearchClient,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MobileVerificationUiState>(MobileVerificationUiState.VerifyPhone)
    val uiState: StateFlow<MobileVerificationUiState> = _uiState

    var showProgress by mutableStateOf(false)

    /**
     * Verify Mobile number that it already exist or not then request otp
     */
    fun verifyMobileAndRequestOtp(
        fullNumber: String,
        mobileNo: String,
        onError: (String?) -> Unit,
    ) {
        showProgress = true
        mUseCaseHandler.execute(
            searchClientUseCase,
            fullNumber.let { SearchClient.RequestValues(it) },
            object : UseCase.UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    onError("Mobile number already exists.")
                    showProgress = false
                }

                override fun onError(message: String) {
                    requestOtp(fullNumber)
                }
            },
        )
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
