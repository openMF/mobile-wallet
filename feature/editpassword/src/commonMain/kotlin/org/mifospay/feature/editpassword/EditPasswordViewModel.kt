/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.editpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mobile_wallet.feature.editpassword.generated.resources.Res
import mobile_wallet.feature.editpassword.generated.resources.feature_editpassword_error_empty_current_password
import mobile_wallet.feature.editpassword.generated.resources.feature_editpassword_error_password_min_length
import mobile_wallet.feature.editpassword.generated.resources.feature_editpassword_error_password_mismatch
import mobile_wallet.feature.editpassword.generated.resources.feature_editpassword_error_password_weak
import mobile_wallet.feature.editpassword.generated.resources.feature_editpassword_error_same_password
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.ui.PasswordStrengthState
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.core.ui.utils.PasswordChecker
import org.mifospay.core.ui.utils.PasswordStrength
import org.mifospay.core.ui.utils.PasswordStrengthResult
import org.mifospay.feature.editpassword.EditPasswordAction.Internal.ReceivePasswordStrengthResult
import org.mifospay.feature.editpassword.EditPasswordAction.Internal.ReceiveUpdatePasswordResult
import org.mifospay.feature.editpassword.EditPasswordDialog.Error

internal class EditPasswordViewModel(
    private val userRepository: UserRepository,
    userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditPasswordState, EditPasswordEvent, EditPasswordAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: EditPasswordState(),
) {

    companion object {
        private const val KEY_STATE = "state"
        private const val MIN_PASSWORD_LENGTH = 8
    }

    private val userInfo = userPreferencesRepository.userInfo.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    private var passwordStrengthJob: Job = Job().apply { complete() }

    init {
        stateFlow
            .onEach {
                savedStateHandle.setSerialized(key = KEY_STATE, value = it)
            }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: EditPasswordAction) {
        when (action) {
            is EditPasswordAction.CurrentPasswordChange -> {
                mutableStateFlow.update {
                    it.copy(currentPasswordInput = action.currentPassword)
                }
            }

            is EditPasswordAction.NewPasswordChange -> handleNewPasswordInput(action)

            is EditPasswordAction.ConfirmPasswordChange -> {
                mutableStateFlow.update {
                    it.copy(confirmPasswordInput = action.confirmPassword)
                }
            }

            EditPasswordAction.ErrorDialogDismiss -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is ReceivePasswordStrengthResult -> handlePasswordStrengthResult(action)

            is ReceiveUpdatePasswordResult -> handleResult(action)

            EditPasswordAction.SubmitClick -> handleSubmitClick()

            is EditPasswordAction.NavigateBackClick -> {
                sendEvent(EditPasswordEvent.NavigateBack)
            }
        }
    }

    private fun handleNewPasswordInput(action: EditPasswordAction.NewPasswordChange) {
        // Update input:
        mutableStateFlow.update { it.copy(newPasswordInput = action.newPassword) }
        // Update password strength:
        passwordStrengthJob.cancel()
        if (action.newPassword.isEmpty()) {
            mutableStateFlow.update {
                it.copy(passwordStrengthState = PasswordStrengthState.NONE)
            }
        } else {
            passwordStrengthJob = viewModelScope.launch {
                val result = PasswordChecker.getPasswordStrengthResult(action.newPassword)
                trySendAction(ReceivePasswordStrengthResult(result))
            }
        }
    }

    private fun handlePasswordStrengthResult(action: ReceivePasswordStrengthResult) {
        when (val result = action.result) {
            is PasswordStrengthResult.Success -> {
                val updatedState = when (result.passwordStrength) {
                    PasswordStrength.LEVEL_0 -> PasswordStrengthState.WEAK_1
                    PasswordStrength.LEVEL_1 -> PasswordStrengthState.WEAK_2
                    PasswordStrength.LEVEL_2 -> PasswordStrengthState.WEAK_3
                    PasswordStrength.LEVEL_3 -> PasswordStrengthState.GOOD
                    PasswordStrength.LEVEL_4 -> PasswordStrengthState.STRONG
                    PasswordStrength.LEVEL_5 -> PasswordStrengthState.VERY_STRONG
                }
                mutableStateFlow.update { oldState ->
                    oldState.copy(passwordStrengthState = updatedState)
                }
            }

            is PasswordStrengthResult.Error -> {}
        }
    }

    private fun handleResult(action: ReceiveUpdatePasswordResult) {
        when (val result = action.result) {
            is DataState.Success -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                sendEvent(EditPasswordEvent.ShowToast(result.data))
                sendEvent(EditPasswordEvent.OnLogoutUser)
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = EditPasswordDialog.ApiError(result.exception.message.toString()))
                }
            }

            DataState.Loading -> {
                mutableStateFlow.update { it.copy(dialogState = EditPasswordDialog.Loading) }
            }
        }
    }

    private fun handleSubmitClick() = when {
        state.currentPasswordInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error(Res.string.feature_editpassword_error_empty_current_password))
            }
        }

        state.isSamePassword -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error(Res.string.feature_editpassword_error_same_password))
            }
        }

        state.newPasswordInput.length < MIN_PASSWORD_LENGTH -> {
            mutableStateFlow.update {
                it.copy(
                    dialogState = Error(
                        Res.string.feature_editpassword_error_password_min_length,
                        listOf(MIN_PASSWORD_LENGTH)
                    ),
                )
            }
        }

        !state.isPasswordMatch -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error(Res.string.feature_editpassword_error_password_mismatch))
            }
        }

        !state.isPasswordStrong -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error(Res.string.feature_editpassword_error_password_weak))
            }
        }

        else -> initiateUpdatePassword()
    }

    private fun initiateUpdatePassword() {
        mutableStateFlow.update {
            it.copy(dialogState = EditPasswordDialog.Loading)
        }

        updatePassword(state.newPasswordInput)
    }

    private fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            val userId = requireNotNull(userInfo.value?.userId)
            val result = userRepository.updateUserPassword(userId, newPassword)

            sendAction(ReceiveUpdatePasswordResult(result))
        }
    }
}

@Serializable
internal data class EditPasswordState(
    val currentPasswordInput: String = "",
    val newPasswordInput: String = "",
    val confirmPasswordInput: String = "",
    @Transient
    val dialogState: EditPasswordDialog? = null,
    val passwordStrengthState: PasswordStrengthState = PasswordStrengthState.NONE,
) {
    val isPasswordStrong: Boolean
        get() = when (passwordStrengthState) {
            PasswordStrengthState.NONE,
            PasswordStrengthState.WEAK_1,
            PasswordStrengthState.WEAK_2,
            PasswordStrengthState.WEAK_3,
                -> false

            PasswordStrengthState.GOOD,
            PasswordStrengthState.STRONG,
            PasswordStrengthState.VERY_STRONG,
                -> true
        }

    val isPasswordMatch: Boolean
        get() = newPasswordInput == confirmPasswordInput

    val isSamePassword: Boolean
        get() = currentPasswordInput == newPasswordInput
}

internal sealed interface EditPasswordDialog {
    data object Loading : EditPasswordDialog
    data class Error(
        val message: StringResource,
        val formatArgs: List<Any> = emptyList()
    ) : EditPasswordDialog
    data class ApiError(val message: String) : EditPasswordDialog
}

internal sealed interface EditPasswordEvent {
    data object NavigateBack : EditPasswordEvent
    data object OnLogoutUser : EditPasswordEvent
    data class ShowToast(val message: String) : EditPasswordEvent
}

internal sealed interface EditPasswordAction {
    data class CurrentPasswordChange(val currentPassword: String) : EditPasswordAction
    data class NewPasswordChange(val newPassword: String) : EditPasswordAction
    data class ConfirmPasswordChange(val confirmPassword: String) : EditPasswordAction

    data object SubmitClick : EditPasswordAction
    data object NavigateBackClick : EditPasswordAction
    data object ErrorDialogDismiss : EditPasswordAction

    sealed class Internal : EditPasswordAction {
        data class ReceiveUpdatePasswordResult(
            val result: DataState<String>,
        ) : Internal()

        data class ReceivePasswordStrengthResult(
            val result: PasswordStrengthResult,
        ) : Internal()
    }
}