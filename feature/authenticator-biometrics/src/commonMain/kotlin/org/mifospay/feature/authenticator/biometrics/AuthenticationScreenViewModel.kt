/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-passcode-cmp/blob/development/LICENSE
 */
package org.mifospay.feature.authenticator.biometrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifospay.core.data.repository.ChooseAuthOptionRepository
import org.mifospay.core.data.repositoryImpl.REGISTRATION_DATA
import org.mifospay.core.ui.utils.BaseViewModel

class AuthenticationScreenViewModel(
    private val chooseAuthOptionRepository: ChooseAuthOptionRepository,
    private val settings: Settings,
) : BaseViewModel<
        AuthenticationScreenState,
        AuthenticationScreenEvent,
        AuthenticationScreenAction
>(AuthenticationScreenState()) {
    override fun handleAction(action: AuthenticationScreenAction) {
        when (action) {
            is AuthenticationScreenAction.OnClickAuthenticate -> {
                authenticateUser(
                    "Mifos App",
                    action.platformAuthenticationProvider
                )
            }
        }
    }
    private val _authenticationResult = MutableStateFlow<AuthenticationResult?>(null)
    val authenticationResult = _authenticationResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun setAuthenticationResultNull() {
        _authenticationResult.value = null
    }

    fun authenticateUser(appName: String, platformAuthenticationProvider: PlatformAuthenticationProvider) {
        _isLoading.value = true
        viewModelScope.launch {
//            val savedData = chooseAuthOptionRepository.getRegistrationData()
//            _authenticationResult.value = platformAuthenticationProvider.onAuthenticatorClick(appName, savedData)
            _isLoading.value = false
        }
    }

    fun clearUserRegistrationFromApp() {
        settings.remove(REGISTRATION_DATA)
//        chooseAuthOptionRepository.clearAuthOption()
    }

    private fun updateState(update: (AuthenticationScreenState) -> AuthenticationScreenState) {

    }


}

data class AuthenticationScreenState(
    val authenticationResult: AuthenticationResult? = null,
    val authenticatorStatus: Set<PlatformAuthenticatorStatus> = emptySet(),
    val isLoading: Boolean = false,
    val dialogBoxType: DialogBoxType = DialogBoxType.None,
    val dialogBoxMessage: String = "",
)

sealed interface AuthenticationScreenAction {
    data class OnClickAuthenticate(val platformAuthenticationProvider: PlatformAuthenticationProvider): AuthenticationScreenAction
}

sealed interface AuthenticationScreenEvent {
    data object OnAuthenticationSuccess: AuthenticationScreenEvent
    data object OnUserNotRegisteredError: AuthenticationScreenEvent
    data object AuthenticationError: AuthenticationScreenEvent
}



enum class DialogBoxType {
    ERROR,
    NOT_SET,
    NOT_AVAILABLE,
    None,
}
