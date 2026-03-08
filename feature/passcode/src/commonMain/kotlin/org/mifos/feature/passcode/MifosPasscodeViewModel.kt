/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.feature.passcode

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.passcode.generated.resources.Res
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_user_not_registered_error_message
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifos.authenticator.passcode.PasscodeAction
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeState
import org.mifos.authenticator.passcode.PasscodeStep
import org.mifos.authenticator.passcode.PasscodeStorageAdapter
import org.mifospay.core.data.repository.AppLockRepository
import org.mifospay.core.ui.utils.BaseViewModel

class MifosPasscodeViewModel(
    private val passcodeManager: PasscodeManager,
    private val appLockRepository: AppLockRepository,
    private val passcodeStorageAdapter: PasscodeStorageAdapter,
) : BaseViewModel<MifosPasscodeState, MifosPasscodeEvent, MifosPasscodeAction>(
    initialState = MifosPasscodeState(passcodeState = passcodeManager.state.value),
) {

    override fun handleAction(action: MifosPasscodeAction) {
        when (action) {
            is MifosPasscodeAction.OnStart -> {
                if (state.passcodeState.passcodeStep == PasscodeStep.Enter) {
                    appLockRepository.lockApp()
                }
            }

            is MifosPasscodeAction.OnResume -> {
                handleResume(action.systemAuthProvider)
            }

            is MifosPasscodeAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MifosPasscodeAction.ForgotPasscode -> {
                appLockRepository.unlockApp()
                sendEvent(MifosPasscodeEvent.OnForgotButton)
            }

            is MifosPasscodeAction.AuthenticationSuccess -> {
                appLockRepository.unlockApp()
                sendEvent(MifosPasscodeEvent.OnAuthenticationSuccess)
            }

            is MifosPasscodeAction.PasscodeCreation -> {
                appLockRepository.unlockApp()
                sendEvent(MifosPasscodeEvent.OnPasscodeCreation)
            }

            is MifosPasscodeAction.PasscodeRejected -> {
                if (state.passcodeState.passcodeStep == PasscodeStep.Enter) {
                    sendEvent(MifosPasscodeEvent.OnPasscodeRejected)
                }
            }

            is MifosPasscodeAction.PasscodeChanged -> {
                sendEvent(MifosPasscodeEvent.OnPasscodeChanged)
            }

            is MifosPasscodeAction.DisableBiometrics -> {
                sendEvent(MifosPasscodeEvent.OnDisableBiometrics)
            }

            is MifosPasscodeAction.BiometricError -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = PasscodeDialogState.Error(
                            message = action.message,
                        ),
                    )
                }
            }

            is MifosPasscodeAction.UserNotRegistered -> {
                mutableStateFlow.update { it ->
                    it.copy(
                        dialogState = PasscodeDialogState.UserNotRegistered(message = action.message),
                    )
                }
            }

            is MifosPasscodeAction.OnAuthenticatorClick -> {
                viewModelScope.launch {
                    val result =
                        action.systemAuthProvider.onAuthenticatorClick(
                            appName = "Mifos Pay",
                            savedRegistrationData = passcodeStorageAdapter.loadRegistrationData() ?: "",
                        )
                    handleAuthenticationResult(result)
                }
            }

            MifosPasscodeAction.ClickConfirmOnNotRegisteredDialog -> {
                passcodeManager.trySendAction(PasscodeAction.BiometricUserNotRegistered)
            }
        }
    }

    private fun handleResume(
        systemAuthProvider: PlatformAuthenticationProvider,
    ) {
        viewModelScope.launch {
            val biometricsStatus = systemAuthProvider.authenticatorStatus.value
            if (
                biometricsStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET) &&
                state.passcodeState.passcodeStep == PasscodeStep.Enter
            ) {
                val result =
                    passcodeStorageAdapter.loadRegistrationData()?.let { data ->
                        systemAuthProvider.onAuthenticatorClick(
                            appName = "Mifos Pay",
                            savedRegistrationData = data,
                        )
                    }
                handleAuthenticationResult(result)
            }
        }
    }

    private suspend fun handleAuthenticationResult(
        result: AuthenticationResult?,
    ) {
        when (result) {
            is AuthenticationResult.Error -> {
                sendAction(MifosPasscodeAction.BiometricError(result.message))
            }

            AuthenticationResult.Success -> {
                passcodeManager.trySendAction(PasscodeAction.BiometricUnlockSuccess)
            }

            AuthenticationResult.UserNotRegistered -> {
                sendAction(
                    MifosPasscodeAction.UserNotRegistered(
                        getString(Res.string.feature_authenticator_user_not_registered_error_message),
                    ),
                )
            }

            null -> {}
        }
    }
}

data class MifosPasscodeState(
    val passcodeState: PasscodeState,
    val dialogState: PasscodeDialogState? = null,
    val showBiometricsKeyButton: Boolean = true,
)

sealed interface PasscodeDialogState {
    data class Error(val message: String) : PasscodeDialogState
    data class UserNotRegistered(
        val message: String,
    ) : PasscodeDialogState
}

sealed interface MifosPasscodeAction {
    data object OnStart : MifosPasscodeAction
    data class OnResume(
        val systemAuthProvider: PlatformAuthenticationProvider,
    ) : MifosPasscodeAction

    data object DismissDialog : MifosPasscodeAction
    data object ForgotPasscode : MifosPasscodeAction
    data object AuthenticationSuccess : MifosPasscodeAction
    data object PasscodeCreation : MifosPasscodeAction
    data object PasscodeRejected : MifosPasscodeAction
    data object PasscodeChanged : MifosPasscodeAction
    data object DisableBiometrics : MifosPasscodeAction
    data class BiometricError(val message: String) : MifosPasscodeAction
    data class UserNotRegistered(val message: String) : MifosPasscodeAction
    data object ClickConfirmOnNotRegisteredDialog : MifosPasscodeAction
    data class OnAuthenticatorClick(
        val systemAuthProvider: PlatformAuthenticationProvider,
    ) : MifosPasscodeAction
}

sealed interface MifosPasscodeEvent {
    data object OnAuthenticationSuccess : MifosPasscodeEvent
    data object OnForgotButton : MifosPasscodeEvent
    data object OnPasscodeCreation : MifosPasscodeEvent
    data object OnPasscodeRejected : MifosPasscodeEvent
    data object OnPasscodeChanged : MifosPasscodeEvent
    data object OnDisableBiometrics : MifosPasscodeEvent
}
