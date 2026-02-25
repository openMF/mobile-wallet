/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.chooseAuthOption

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.RegistrationResult
import org.mifospay.core.data.repository.ChooseAuthOptionRepository
import org.mifospay.core.data.util.AppLockOption
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.client.Client
import template.core.base.ui.BaseViewModel

private const val USER_ID = "mifosUser"
private const val USER_EMAIL = "mifospay@mifos.org"
private const val DISPLAY_NAME = "Mifos Pay User"

class ChooseAuthOptionScreenViewmodel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val chooseAuthOptionRepository: ChooseAuthOptionRepository,
) : BaseViewModel<
    ChooseAuthOptionScreenUiState,
    ChooseAuthOptionScreenEvents,
    ChooseAuthOptionScreenAction,
    >(ChooseAuthOptionScreenUiState(client = userPreferencesRepository.client.value)) {
    override fun handleAction(action: ChooseAuthOptionScreenAction) {
        when (action) {
            ChooseAuthOptionScreenAction.OnSelectDeviceLock -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedAuthOption = AppLockOption.DeviceLock,
                    )
                }
            }
            ChooseAuthOptionScreenAction.OnSelectPasscode -> {
                mutableStateFlow.update {
                    it.copy(selectedAuthOption = AppLockOption.MifosPasscode)
                }
            }

            is ChooseAuthOptionScreenAction.RegisterUserBiometrics -> {
                registerUser(
                    platformAuthenticationProvider = action.platformAuthenticationProvider,
                    userID = mutableStateFlow.value.client?.id?.run { toString() } ?: USER_ID,
                    userEmail = mutableStateFlow.value.client?.emailAddress ?: USER_EMAIL,
                    displayName = mutableStateFlow.value.client?.displayName ?: DISPLAY_NAME,
                )
            }

            ChooseAuthOptionScreenAction.DismissDialogBox -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = null,
                    )
                }
            }

            is ChooseAuthOptionScreenAction.SetupPlatformAuthenticator -> {
                action.platformAuthenticationProvider.setupPlatformAuthenticator()
            }

            ChooseAuthOptionScreenAction.NavigateToPasscode -> {
                saveAppLockOption(AppLockOption.MifosPasscode)
                sendEvent(ChooseAuthOptionScreenEvents.OnNavigateToPasscode)
            }
        }
    }

    private fun setRegistrationResultNull() {
        mutableStateFlow.update {
            it.copy(
                registrationResult = null,
            )
        }
    }

    private fun registerUser(
        platformAuthenticationProvider: PlatformAuthenticationProvider,
        userID: String,
        userEmail: String,
        displayName: String,
    ) {
        viewModelScope.launch {
            platformAuthenticationProvider.updateAuthenticatorStatus()
            val registrationResult = platformAuthenticationProvider.registerUser(
                userID,
                userEmail,
                displayName,
            )

            mutableStateFlow.update {
                it.copy(
                    registrationResult = registrationResult,
                )
            }

            when (registrationResult) {
                is RegistrationResult.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ChooseAuthOptionScreenUiState.ScreenState.Error(registrationResult.message),
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotAvailable -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ChooseAuthOptionScreenUiState.ScreenState.Error("Option not available"),
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotSet -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ChooseAuthOptionScreenUiState.ScreenState.AuthenticatorNotSetup,
                        )
                    }
                }
                is RegistrationResult.Success -> {
                    mutableStateFlow.update {
                        it.copy(screenState = null)
                    }
                    saveAppLockOption(AppLockOption.DeviceLock)
                    saveRegistrationData(registrationResult.message)
                    sendEvent(ChooseAuthOptionScreenEvents.BiometricRegistrationSuccess)
                    setRegistrationResultNull()
                }
            }
        }
    }

    private fun saveRegistrationData(registrationData: String) =
        chooseAuthOptionRepository.saveBiometricRegistrationData(registrationData)

    private fun saveAppLockOption(appLock: AppLockOption) {
        chooseAuthOptionRepository.setAuthOption(appLock)
    }
}

data class ChooseAuthOptionScreenUiState(
    val registrationResult: RegistrationResult? = null,
    val screenState: ScreenState? = null,
    val selectedAuthOption: AppLockOption = AppLockOption.None,
    val client: Client? = null,
) {
    sealed interface ScreenState {
        data object AuthenticatorNotSetup : ScreenState
        data class Error(val message: String) : ScreenState
    }
}

sealed interface ChooseAuthOptionScreenEvents {
    data object BiometricRegistrationSuccess : ChooseAuthOptionScreenEvents
    data object OnNavigateToPasscode : ChooseAuthOptionScreenEvents
}

sealed interface ChooseAuthOptionScreenAction {
    data class SetupPlatformAuthenticator(val platformAuthenticationProvider: PlatformAuthenticationProvider) : ChooseAuthOptionScreenAction
    data class RegisterUserBiometrics(val platformAuthenticationProvider: PlatformAuthenticationProvider) : ChooseAuthOptionScreenAction
    data object OnSelectDeviceLock : ChooseAuthOptionScreenAction
    data object OnSelectPasscode : ChooseAuthOptionScreenAction
    data object NavigateToPasscode : ChooseAuthOptionScreenAction
    data object DismissDialogBox : ChooseAuthOptionScreenAction
}
