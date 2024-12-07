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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.SearchRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.auth.mobileVerify.MobileVerificationAction.Internal.ReceiveOtpVerifyResult

private const val KEY_STATE = "mobile_verification"

class MobileVerificationViewModel(
    private val searchRepository: SearchRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MobileVerificationState, MobileVerificationEvent, MobileVerificationAction>(
    initialState = savedStateHandle[KEY_STATE] ?: MobileVerificationState.VerifyPhoneState(),
) {

//    init {
//        stateFlow.onEach { savedStateHandle[KEY_STATE] = it }.launchIn(viewModelScope)
//    }

    override fun handleAction(action: MobileVerificationAction) {
        when (action) {
            is MobileVerificationAction.PhoneNoChanged -> {
                mutableStateFlow.update {
                    if (it is MobileVerificationState.VerifyPhoneState) {
                        it.copy(phoneNo = action.phoneNo)
                    } else {
                        it
                    }
                }
            }

            is MobileVerificationAction.VerifyPhoneBtnClicked -> {
                val currentState = state as? MobileVerificationState.VerifyPhoneState ?: return
                verifyPhoneNo(currentState)
            }

            is MobileVerificationAction.OtpChanged -> {
                mutableStateFlow.update {
                    if (it is MobileVerificationState.VerifyOtpState) {
                        it.copy(otp = action.otp)
                    } else {
                        it
                    }
                }
            }

            is MobileVerificationAction.VerifyOtpBtnClicked -> {
                val currentState = state as? MobileVerificationState.VerifyOtpState ?: return
                verifyEnteredOTP(currentState)
            }

            is ReceiveOtpVerifyResult -> handleOtpVerifyResult(action)

            is MobileVerificationAction.ChangePhoneNumber -> handleChangePhoneNoClick()

            is MobileVerificationAction.DismissDialog -> handleDismissDialog()

            is MobileVerificationAction.CloseButtonClick -> handleCloseButtonClick()
        }
    }

    private fun verifyPhoneNo(currentState: MobileVerificationState.VerifyPhoneState) {
        if (currentState.isPhoneNoValid) {
            verifyMobileAndRequestOtp(currentState.phoneNo, currentState)
        } else {
            mutableStateFlow.update {
                currentState.copy(
                    dialogState = MobileVerificationState.DialogState.Error("Phone no isn't valid"),
                )
            }
        }
    }

    private fun verifyMobileAndRequestOtp(
        phoneNo: String,
        currentState: MobileVerificationState.VerifyPhoneState,
    ) {
        viewModelScope.launch {
            val result = searchRepository.searchResources(
                query = phoneNo,
                resources = Constants.CLIENTS,
                exactMatch = true,
            )

            when (result) {
                is DataState.Error -> {
                    val message = result.exception.message ?: "Something Went Wrong!"
                    mutableStateFlow.update {
                        currentState.copy(
                            dialogState = MobileVerificationState.DialogState.Error(message),
                        )
                    }
                }

                is DataState.Loading -> {
                    mutableStateFlow.update {
                        currentState.copy(dialogState = MobileVerificationState.DialogState.Loading)
                    }
                }

                is DataState.Success -> {
                    if (result.data.isEmpty()) {
                        requestAnOtpToPhoneNo(phoneNo)
                    } else {
                        val message = "Mobile number already exists."
                        mutableStateFlow.update {
                            currentState.copy(
                                dialogState = MobileVerificationState.DialogState.Error(message),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestAnOtpToPhoneNo(phoneNo: String) {
        viewModelScope.launch {
            // TODO:: Call repository request an otp to this phone no.
            mutableStateFlow.update {
                MobileVerificationState.VerifyOtpState(phoneNo)
            }
            trySendAction(MobileVerificationAction.DismissDialog)
        }
    }

    private fun verifyEnteredOTP(state: MobileVerificationState.VerifyOtpState) {
        viewModelScope.launch {
            if (state.isOtpValid) {
                // TODO:: Match send otp to entered otp
                mutableStateFlow.update {
                    state.copy(
                        dialogState = MobileVerificationState.DialogState.Loading,
                    )
                }

                sendAction(ReceiveOtpVerifyResult(DataState.Success(state.phoneNo)))
            } else {
                mutableStateFlow.update {
                    state.copy(
                        dialogState = MobileVerificationState.DialogState.Error("OTP isn't valid"),
                    )
                }
            }
        }
    }

    private fun handleOtpVerifyResult(action: ReceiveOtpVerifyResult) {
        when (action.loginResult) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    (state as? MobileVerificationState.VerifyOtpState)?.copy(
                        dialogState = MobileVerificationState.DialogState.Error("Otp Verification Failed"),
                    ) ?: state
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    (state as? MobileVerificationState.VerifyOtpState)?.copy(
                        dialogState = MobileVerificationState.DialogState.Loading,
                    ) ?: state
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    (state as? MobileVerificationState.VerifyOtpState)?.copy(
                        dialogState = null,
                    ) ?: state
                }
                sendEvent(MobileVerificationEvent.ShowToast("Otp Verified Successfully"))
                sendEvent(MobileVerificationEvent.NavigateToSignup(action.loginResult.data))
            }
        }
    }

    private fun handleChangePhoneNoClick() {
        viewModelScope.launch {
            mutableStateFlow.update {
                if (it is MobileVerificationState.VerifyOtpState) {
                    MobileVerificationState.VerifyPhoneState(phoneNo = it.phoneNo)
                } else {
                    it
                }
            }
        }
    }

    private fun handleDismissDialog() {
        viewModelScope.launch {
            mutableStateFlow.update {
                when (it) {
                    is MobileVerificationState.VerifyOtpState -> {
                        it.copy(dialogState = null)
                    }

                    is MobileVerificationState.VerifyPhoneState -> {
                        it.copy(dialogState = null)
                    }
                }
            }
        }
    }

    private fun handleCloseButtonClick() {
        viewModelScope.launch {
            if (state is MobileVerificationState.VerifyOtpState) {
                sendEvent(MobileVerificationEvent.NavigateBack)
            } else {
                sendAction(MobileVerificationAction.ChangePhoneNumber)
            }
        }
    }
}

sealed class MobileVerificationState : Parcelable {
    @Parcelize
    data class VerifyPhoneState(
        val phoneNo: String = "",
        val dialogState: DialogState? = null,
    ) : MobileVerificationState() {
        val isPhoneNoValid: Boolean
            get() = phoneNo.length == 10
    }

    @Parcelize
    data class VerifyOtpState(
        val phoneNo: String,
        val otp: String = "",
        val dialogState: DialogState? = null,
    ) : MobileVerificationState() {
        val isOtpValid: Boolean
            get() = otp.length == 6
    }

    sealed class DialogState : Parcelable {
        @Parcelize
        data class Error(val message: String) : DialogState()

        @Parcelize
        data object Loading : DialogState()
    }
}

sealed interface MobileVerificationEvent {
    data object NavigateBack : MobileVerificationEvent
    data class NavigateToSignup(val phoneNo: String) : MobileVerificationEvent
    data class ShowToast(val message: String) : MobileVerificationEvent
}

sealed interface MobileVerificationAction {
    data class PhoneNoChanged(val phoneNo: String) : MobileVerificationAction
    data object VerifyPhoneBtnClicked : MobileVerificationAction

    data class OtpChanged(val otp: String) : MobileVerificationAction
    data object ChangePhoneNumber : MobileVerificationAction
    data object VerifyOtpBtnClicked : MobileVerificationAction

    data object DismissDialog : MobileVerificationAction
    data object CloseButtonClick : MobileVerificationAction

    sealed class Internal : MobileVerificationAction {
        data class ReceiveOtpVerifyResult(
            val loginResult: DataState<String>,
        ) : Internal()
    }
}
