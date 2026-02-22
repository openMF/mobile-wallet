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
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.data.repository.ChooseAuthOptionRepository
import org.mifospay.core.model.client.Client
import org.mifospay.core.data.util.AppLockOption
import template.core.base.ui.BaseViewModel

private const val USER_ID = "mifosUser"
private const val USER_EMAIL = "mifos@mifos.org"
private const val DISPLAY_NAME = "XYZ"

class ChooseAuthOptionScreenViewmodel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val chooseAuthOptionRepository: ChooseAuthOptionRepository,
) : BaseViewModel<
    ChooseAuthOptionScreenUiState,
    ChooseAuthOptionScreenEvents,
    ChooseAuthOptionScreenActions,
    >(ChooseAuthOptionScreenUiState(client = userPreferencesRepository.client.value)) {
    override fun handleAction(action: ChooseAuthOptionScreenActions) {
        when (action) {
            ChooseAuthOptionScreenActions.OnSelectDeviceLock -> {
//                mutableStateFlow.update {
//                    it.copy(
//                        selectedAuthOption = AppLockOption.DeviceLock,
//                    )
//                }
                mutableStateFlow.update {
                    it.copy(
                        dialogBoxType = DialogBoxType.NOT_AVAILABLE,
                    )
                }
            }
            ChooseAuthOptionScreenActions.OnSelectPasscode -> {
                mutableStateFlow.update {
                    it.copy(selectedAuthOption = AppLockOption.MifosPasscode)
                }
                saveAppLockOption(AppLockOption.MifosPasscode)
            }

            is ChooseAuthOptionScreenActions.RegisterUserBiometrics -> {
                registerUser(
                    platformAuthenticationProvider = action.platformAuthenticationProvider,
                    userID = mutableStateFlow.value.client?.id?.run { toString() } ?: USER_ID,
                    userEmail = mutableStateFlow.value.client?.emailAddress ?: USER_EMAIL,
                    displayName = mutableStateFlow.value.client?.displayName ?: DISPLAY_NAME,
                )
            }

            ChooseAuthOptionScreenActions.DismissDialogBox -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogBoxType = DialogBoxType.None,
                    )
                }
            }

            is ChooseAuthOptionScreenActions.SetupPlatformAuthenticator -> {
                action.platformAuthenticationProvider.setupPlatformAuthenticator()
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
                            dialogBoxType = DialogBoxType.ERROR,
                            dialogBoxMessage = registrationResult.message,
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotAvailable -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogBoxType = DialogBoxType.NOT_AVAILABLE,
                            dialogBoxMessage = "Option Not available",
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotSet -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogBoxType = DialogBoxType.NOT_SET,
                            dialogBoxMessage = "Platform authenticator not set.",
                        )
                    }
                }
                is RegistrationResult.Success -> {
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
    val dialogBoxType: DialogBoxType = DialogBoxType.None,
    val dialogBoxMessage: String = "",
    val selectedAuthOption: AppLockOption = AppLockOption.None,
    val client: Client? = null,
)

sealed interface ChooseAuthOptionScreenEvents {
    data object BiometricRegistrationSuccess : ChooseAuthOptionScreenEvents
    data object OnChoosePasscode : ChooseAuthOptionScreenEvents
}

sealed interface ChooseAuthOptionScreenActions {
    data class SetupPlatformAuthenticator(val platformAuthenticationProvider: PlatformAuthenticationProvider) : ChooseAuthOptionScreenActions
    data class RegisterUserBiometrics(val platformAuthenticationProvider: PlatformAuthenticationProvider) : ChooseAuthOptionScreenActions
    data object OnSelectDeviceLock : ChooseAuthOptionScreenActions
    data object OnSelectPasscode : ChooseAuthOptionScreenActions
    data object DismissDialogBox : ChooseAuthOptionScreenActions
}

enum class DialogBoxType {
    ERROR,
    NOT_SET,
    NOT_AVAILABLE,
    None,
}
