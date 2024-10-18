/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.domain.LoginUseCase
import org.mifospay.core.model.user.UserInfo
import org.mifospay.core.ui.utils.BaseViewModel

private const val KEY_STATE = "state"

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = savedStateHandle[KEY_STATE] ?: LoginState(dialogState = null),
) {

    init {
        savedStateHandle.get<String>("username")?.let {
            trySendAction(LoginAction.UsernameChanged(it))
        }
    }

    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.UsernameChanged -> {
                mutableStateFlow.update {
                    it.copy(username = action.username)
                }
            }

            is LoginAction.PasswordChanged -> {
                mutableStateFlow.update {
                    it.copy(password = action.password)
                }
            }

            is LoginAction.TogglePasswordVisibility -> {
                mutableStateFlow.update {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }

            is LoginAction.LoginClicked -> {
                loginUser(state.username, state.password)
            }

            is LoginAction.Internal.ReceiveLoginResult -> {
                handleLoginResult(action)
            }

            is LoginAction.SignupClicked -> {
                sendEvent(LoginEvent.NavigateToSignup)
            }

            is LoginAction.ErrorDialogDismiss -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
            }
        }
    }

    private fun handleLoginResult(action: LoginAction.Internal.ReceiveLoginResult) {
        when (action.loginResult) {
            is DataState.Error -> {
                val message = action.loginResult.exception.message ?: ""

                mutableStateFlow.update {
                    it.copy(dialogState = LoginState.DialogState.Error(message))
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = LoginState.DialogState.Loading)
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                sendEvent(LoginEvent.NavigateToPasscodeScreen)
            }
        }
    }

    private fun loginUser(
        username: String,
        password: String,
    ) {
        mutableStateFlow.update {
            it.copy(dialogState = LoginState.DialogState.Loading)
        }

        viewModelScope.launch {
            val result = loginUseCase(username, password)
            sendAction(LoginAction.Internal.ReceiveLoginResult(result))
        }
    }
}

@Parcelize
data class LoginState(
    val username: String = "",
    @IgnoredOnParcel
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val dialogState: DialogState?,
) : Parcelable {
    sealed class DialogState : Parcelable {
        @Parcelize
        data class Error(val message: String) : DialogState()

        @Parcelize
        data object Loading : DialogState()
    }
}

sealed class LoginEvent {
    data object NavigateBack : LoginEvent()
    data object NavigateToSignup : LoginEvent()
    data object NavigateToPasscodeScreen : LoginEvent()
    data class ShowToast(val message: String) : LoginEvent()
}

sealed class LoginAction {
    data class UsernameChanged(val username: String) : LoginAction()
    data class PasswordChanged(val password: String) : LoginAction()
    data object TogglePasswordVisibility : LoginAction()
    data object ErrorDialogDismiss : LoginAction()
    data object LoginClicked : LoginAction()
    data object SignupClicked : LoginAction()

    sealed class Internal : LoginAction() {
        data class ReceiveLoginResult(
            val loginResult: DataState<UserInfo>,
        ) : Internal()
    }
}
