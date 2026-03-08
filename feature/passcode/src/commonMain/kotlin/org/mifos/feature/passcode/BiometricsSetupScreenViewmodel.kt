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
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometrics_available
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometrics_not_set
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.RegistrationResult
import org.mifos.authenticator.passcode.PasscodeAction
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.client.Client
import org.mifospay.core.ui.utils.AuthenticationUtils
import org.mifospay.core.ui.utils.BaseViewModel

class BiometricSetupScreenViewmodel(
    userPreferencesRepository: UserPreferencesRepository,
    private val passcodeManager: PasscodeManager,
) : BaseViewModel<
    BiometricSetupScreenState,
    BiometricSetupScreenEvent,
    BiometricSetupScreenAction,
    >(BiometricSetupScreenState(client = userPreferencesRepository.client.value)) {
    override fun handleAction(action: BiometricSetupScreenAction) {
        when (action) {
            is BiometricSetupScreenAction.ClickSetupBiometric -> {
                registerUser(action.platformAuthenticationProvider)
            }
            BiometricSetupScreenAction.ClickSkipBiometric -> {
                sendEvent(BiometricSetupScreenEvent.OnSkipBiometricSetup)
            }

            BiometricSetupScreenAction.DismissErrorDialog -> {
                mutableStateFlow.update {
                    it.copy(error = null)
                }
            }
        }
    }

    private fun registerUser(platformAuthenticationProvider: PlatformAuthenticationProvider) {
        viewModelScope.launch {
            val result = platformAuthenticationProvider.registerUser(
                mutableStateFlow.value.client?.id?.run { toString() } ?: AuthenticationUtils.DEFAULT_USER_ID,
                mutableStateFlow.value.client?.emailAddress ?: AuthenticationUtils.DEFAULT_USER_EMAIL,
                mutableStateFlow.value.client?.displayName ?: AuthenticationUtils.DEFAULT_DISPLAY_NAME,
            )

            when (result) {
                is RegistrationResult.Success -> {
                    passcodeManager.trySendAction(PasscodeAction.SaveBiometricRegistration(result.message))
                    sendEvent(BiometricSetupScreenEvent.OnBiometricSetupSuccess)
                }
                RegistrationResult.PlatformAuthenticatorNotSet -> {
                    mutableStateFlow.update {
                        it.copy(
                            error = getString(Res.string.feature_authenticator_biometrics_not_set),
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotAvailable -> {
                    mutableStateFlow.update {
                        it.copy(
                            error = getString(Res.string.feature_authenticator_biometrics_available),
                        )
                    }
                }
                is RegistrationResult.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            error = result.message,
                        )
                    }
                }
            }
        }
    }
}

data class BiometricSetupScreenState(
    val client: Client? = null,
    val error: String? = null,
)

sealed interface BiometricSetupScreenAction {
    data object DismissErrorDialog : BiometricSetupScreenAction
    data object ClickSkipBiometric : BiometricSetupScreenAction

    data class ClickSetupBiometric(
        val platformAuthenticationProvider: PlatformAuthenticationProvider,
    ) : BiometricSetupScreenAction
}

sealed interface BiometricSetupScreenEvent {
    data object OnSkipBiometricSetup : BiometricSetupScreenEvent
    data object OnBiometricSetupSuccess : BiometricSetupScreenEvent
}
