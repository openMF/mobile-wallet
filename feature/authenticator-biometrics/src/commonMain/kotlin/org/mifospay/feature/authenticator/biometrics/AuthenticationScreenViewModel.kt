/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.authenticator.biometrics

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifospay.core.data.repository.ChooseAuthOptionRepository
import org.mifospay.core.data.repository.PlatformAuthenticationDataRepository
import org.mifospay.core.ui.utils.BaseViewModel

class AuthenticationScreenViewModel(
    private val chooseAuthOptionRepository: ChooseAuthOptionRepository,
    private val platformAuthenticationDataRepository: PlatformAuthenticationDataRepository,
) : BaseViewModel<
    AuthenticationScreenState,
    AuthenticationScreenEvent,
    AuthenticationScreenAction,
    >(AuthenticationScreenState()) {
    override fun handleAction(action: AuthenticationScreenAction) {
        when (action) {
            is AuthenticationScreenAction.OnClickAuthenticate -> {
                authenticateUser(
                    "Mifos App",
                    action.platformAuthenticationProvider,
                )
            }

            AuthenticationScreenAction.OkayOnUserNotRegisteredError -> {
                updateState {
                    it.copy(screenState = null)
                }
                sendEvent(AuthenticationScreenEvent.OnForceLogout)
            }

            AuthenticationScreenAction.AuthenticatorStatusNotSetup -> {
                sendEvent(AuthenticationScreenEvent.OnForceLogout)
            }
            AuthenticationScreenAction.OnDismissDialog -> {
                updateState { it.copy(screenState = null) }
            }
        }
    }

    private fun authenticateUser(appName: String, platformAuthenticationProvider: PlatformAuthenticationProvider) {
        viewModelScope.launch {
            updateState {
                it.copy(screenState = AuthenticationScreenState.ScreenState.Loading)
            }
            platformAuthenticationProvider.updateAuthenticatorStatus()
            val savedData = platformAuthenticationDataRepository.getBiometricRegistrationData()
            val authResult = platformAuthenticationProvider.onAuthenticatorClick(appName, savedData)

            when (authResult) {
                is AuthenticationResult.Error -> {
                    updateState {
                        it.copy(
                            screenState = AuthenticationScreenState.ScreenState.Error(authResult.message),
                        )
                    }
                }
                AuthenticationResult.Success -> {
                    updateState {
                        it.copy(
                            screenState = null,
                        )
                    }
                    sendEvent(AuthenticationScreenEvent.OnAuthenticationSuccess)
                }
                AuthenticationResult.UserNotRegistered -> {
                    updateState {
                        it.copy(
                            screenState = AuthenticationScreenState.ScreenState.UserNotRegistered("The user has changed authentication settings, register again."),
                        )
                    }
                    clearUserRegistrationFromApp()
                }
            }
        }
    }

    private fun clearUserRegistrationFromApp() {
        platformAuthenticationDataRepository.clearBiometricRegistrationData()
        chooseAuthOptionRepository.removeAuthOption()
    }

    private fun updateState(update: (AuthenticationScreenState) -> AuthenticationScreenState) {
        mutableStateFlow.update {
            update(it)
        }
    }
}

data class AuthenticationScreenState(
    val authenticatorStatus: Set<PlatformAuthenticatorStatus> = emptySet(),
    val screenState: ScreenState? = null,
) {
    sealed interface ScreenState {
        data class Error(val message: String) : ScreenState
        data class UserNotRegistered(val message: String) : ScreenState
        data object Loading : ScreenState
    }
}

sealed interface AuthenticationScreenAction {
    data class OnClickAuthenticate(val platformAuthenticationProvider: PlatformAuthenticationProvider) : AuthenticationScreenAction
    data object OkayOnUserNotRegisteredError : AuthenticationScreenAction
    data object AuthenticatorStatusNotSetup : AuthenticationScreenAction
    data object OnDismissDialog : AuthenticationScreenAction
}

sealed interface AuthenticationScreenEvent {
    data object OnAuthenticationSuccess : AuthenticationScreenEvent
    data object OnForceLogout : AuthenticationScreenEvent
}

enum class DialogBoxType {
    ERROR,
    NOT_SET,
    NOT_AVAILABLE,
    None,
}
