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

/**
 * ViewModel responsible for managing login-related UI state and actions.
 *
 * @param loginUseCase Use case that performs the login operation.
 * @param savedStateHandle Provides access to saved state.
 */
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

    /**
     * Handles incoming actions from the UI.
     *
     * @param action The [LoginAction] to process.
     */
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

    /**
     * Handles the result of the login attempt.
     *
     * @param action The internal login result action.
     */
    private fun handleLoginResult(action: LoginAction.Internal.ReceiveLoginResult) {
        when (action.loginResult) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = LoginState.DialogState.Error(action.loginResult.message))
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

    /**
     * Initiates the login process.
     *
     * @param username The user's username.
     * @param password The user's password.
     */
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

/**
 * Represents the UI state of the login screen.
 *
 * @property username The entered username.
 * @property password The entered password (excluded from parceling).
 * @property isPasswordVisible Indicates whether the password is visible.
 * @property dialogState Represents loading or error dialog state.
 */
@Parcelize
data class LoginState(
    val username: String = "",
    @IgnoredOnParcel
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val dialogState: DialogState?,
) : Parcelable {

    /**
     * Represents dialog states shown on the login screen.
     */
    sealed class DialogState : Parcelable {
        /**
         * Represents an error dialog.
         * @param message The error message to display.
         */
        @Parcelize
        data class Error(val message: String) : DialogState()

        /**
         * Represents a loading dialog.
         */
        @Parcelize
        data object Loading : DialogState()
    }
}

/**
 * Defines one-time events to be triggered from the login screen.
 */
sealed class LoginEvent {
    /**
     * Event to navigate back from the login screen.
     */
    data object NavigateBack : LoginEvent()

    /**
     * Event to navigate to the signup screen.
     */
    data object NavigateToSignup : LoginEvent()

    /**
     * Event to navigate to the passcode screen after successful login.
     */
    data object NavigateToPasscodeScreen : LoginEvent()

    /**
     * Event to show a toast message.
     *
     * @param message The message to display.
     */
    data class ShowToast(val message: String) : LoginEvent()
}

/**
 * Represents all user interactions and internal actions on the login screen.
 */
sealed class LoginAction {
    /**
     * Action for when the username is changed.
     */
    data class UsernameChanged(val username: String) : LoginAction()

    /**
     * Action for when the password is changed.
     */
    data class PasswordChanged(val password: String) : LoginAction()

    /**
     * Action to toggle password visibility.
     */
    data object TogglePasswordVisibility : LoginAction()

    /**
     * Action to dismiss the error dialog.
     */
    data object ErrorDialogDismiss : LoginAction()

    /**
     * Action for when the login button is clicked.
     */
    data object LoginClicked : LoginAction()

    /**
     * Action for when the signup button is clicked.
     */
    data object SignupClicked : LoginAction()

    /**
     * Internal actions used within the ViewModel.
     */
    sealed class Internal : LoginAction() {
        /**
         * Result from the login use case.
         *
         * @param loginResult The result of the login attempt.
         */
        data class ReceiveLoginResult(
            val loginResult: DataState<UserInfo>,
        ) : Internal()
    }
}
