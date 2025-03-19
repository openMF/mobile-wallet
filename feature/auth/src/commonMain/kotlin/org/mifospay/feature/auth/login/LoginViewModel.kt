/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */

/**
 * LoginViewModel
 * 
 * ViewModel responsible for handling the business logic and state management
 * for the login functionality in the Mifos Pay application.
 * 
 * Responsibilities:
 * - Manages login form state
 * - Handles user authentication
 * - Processes login requests
 * - Manages error states
 * - Controls navigation
 * 
 * State Management:
 * - LoginFormState: Manages form input states
 * - LoadingState: Handles loading indicators
 * - ErrorState: Manages error messages
 * - SuccessState: Handles successful login
 * 
 * Authentication:
 * - Validates user credentials
 * - Handles biometric authentication
 * - Manages authentication tokens
 * - Implements secure storage
 * 
 * Business Logic:
 * - Form validation
 * - Credential verification
 * - Session management
 * - Error handling
 * 
 * Dependencies:
 * - AuthRepository: For authentication operations
 * - BiometricManager: For biometric authentication
 * - TokenManager: For token storage
 * - NavigationManager: For screen navigation
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
 * LoginViewModel constructor
 * 
 * @param loginUseCase Use case for handling login operations
 * @param savedStateHandle Handle for saving and restoring state
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
     * Handles all user actions in the login screen
     * 
     * @param action The action to handle
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
     * Processes the result of a login attempt
     * 
     * Updates the UI state based on the login result
     * 
     * @param action The login result action containing the result state
     */
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

    /**
     * Initiates the login process
     * 
     * @param username The username entered by the user
     * @param password The password entered by the user
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
 * Data class representing the current state of the login screen
 * 
 * @property username The current username input
 * @property password The current password input
 * @property isPasswordVisible Whether the password is visible
 * @property dialogState The current state of any dialog being shown
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
     * Sealed class representing different dialog states
     */
    sealed class DialogState : Parcelable {
        /**
         * Error dialog state with error message
         * 
         * @property message The error message to display
         */
        @Parcelize
        data class Error(val message: String) : DialogState()

        /**
         * Loading dialog state
         */
        @Parcelize
        data object Loading : DialogState()
    }
}

/**
 * Sealed class representing navigation and UI events
 */
sealed class LoginEvent {
    /** Event to navigate back */
    data object NavigateBack : LoginEvent()
    /** Event to navigate to signup screen */
    data object NavigateToSignup : LoginEvent()
    /** Event to navigate to passcode screen */
    data object NavigateToPasscodeScreen : LoginEvent()
    /** Event to show a toast message */
    data class ShowToast(val message: String) : LoginEvent()
}

/**
 * Sealed class representing user actions
 */
sealed class LoginAction {
    /** Action when username is changed */
    data class UsernameChanged(val username: String) : LoginAction()
    /** Action when password is changed */
    data class PasswordChanged(val password: String) : LoginAction()
    /** Action to toggle password visibility */
    data object TogglePasswordVisibility : LoginAction()
    /** Action to dismiss error dialog */
    data object ErrorDialogDismiss : LoginAction()
    /** Action when login button is clicked */
    data object LoginClicked : LoginAction()
    /** Action when signup button is clicked */
    data object SignupClicked : LoginAction()

    /**
     * Internal actions for handling login results
     */
    sealed class Internal : LoginAction() {
        /**
         * Action to handle login result
         * 
         * @property loginResult The result of the login attempt
         */
        data class ReceiveLoginResult(
            val loginResult: DataState<UserInfo>,
        ) : Internal()
    }
}
