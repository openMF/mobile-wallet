package org.mifos.mobilewallet.mifospay.password.presenter

import androidx.lifecycle.ViewModel
import com.mifos.mobilewallet.model.domain.user.UpdateUserEntityPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser
import org.mifos.mobilewallet.core.domain.usecase.user.UpdateUser
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class EditPasswordViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val authenticateUserUseCase: AuthenticateUser,
    private val updateUserUseCase: UpdateUser
): ViewModel() {

    private val _editPasswordUiState = MutableStateFlow<EditPasswordUiState>(EditPasswordUiState.Loading)
    val editPasswordUiState: StateFlow<EditPasswordUiState> = _editPasswordUiState

//    fun handleSavePasswordButtonStatus(
//        currentPassword: String?,
//        newPassword: String?,
//        newPasswordRepeat: String?
//    ) {
//        if (currentPassword == "" || newPassword == "" || newPasswordRepeat == "") {
//            mEditPasswordView?.disableSavePasswordButton()
//        } else {
//            if (newPassword == newPasswordRepeat) {
//                mEditPasswordView?.enableSavePasswordButton()
//            } else {
//                mEditPasswordView?.disableSavePasswordButton()
//            }
//        }
//    }

    fun updatePassword(
        currentPassword: String?,
        newPassword: String?,
        newPasswordRepeat: String?
    ) {
        _editPasswordUiState.value = EditPasswordUiState.Loading
        if (isNotEmpty(currentPassword) && isNotEmpty(newPassword)
            && isNotEmpty(newPasswordRepeat)
        ) {
            when {
                currentPassword == newPassword -> {
                   _editPasswordUiState.value = EditPasswordUiState.Error(Constants.ERROR_PASSWORDS_CANT_BE_SAME)
                }
                newPassword?.let {
                    newPasswordRepeat?.let { it1 ->
                        isNewPasswordValid(
                            it,
                            it1
                        )
                    }
                } == true -> {
                    if (currentPassword != null) {
                        updatePassword(currentPassword, newPassword)
                    }
                }
                else -> {
                    _editPasswordUiState.value = EditPasswordUiState.Error(Constants.ERROR_VALIDATING_PASSWORD)
                }
            }
        } else {
            _editPasswordUiState.value = EditPasswordUiState.Error(Constants.ERROR_FIELDS_CANNOT_BE_EMPTY)
        }
    }

    private fun isNotEmpty(str: String?): Boolean {
        return !str.isNullOrEmpty()
    }

    private fun isNewPasswordValid(newPassword: String, newPasswordRepeat: String): Boolean {
        return newPassword == newPasswordRepeat
    }

    private fun updatePassword(currentPassword: String, newPassword: String) {
        // authenticate and then update
        mUseCaseHandler.execute(authenticateUserUseCase,
            AuthenticateUser.RequestValues(
                mPreferencesHelper.username,
                currentPassword
            ),
            object : UseCase.UseCaseCallback<AuthenticateUser.ResponseValue> {
                override fun onSuccess(response: AuthenticateUser.ResponseValue) {
                    mUseCaseHandler.execute(updateUserUseCase,
                        UpdateUser.RequestValues(
                            UpdateUserEntityPassword(
                                newPassword
                            ),
                            mPreferencesHelper.userId.toInt()
                        ),
                        object : UseCase.UseCaseCallback<UpdateUser.ResponseValue?> {
                            override fun onSuccess(response: UpdateUser.ResponseValue?) {
                               _editPasswordUiState.value = EditPasswordUiState.Success
                            }

                            override fun onError(message: String) {
                                _editPasswordUiState.value = EditPasswordUiState.Error(message)
                            }
                        })
                }

                override fun onError(message: String) {
                    _editPasswordUiState.value = EditPasswordUiState.Error("Wrong Password")
                }
            })
    }
}

sealed interface EditPasswordUiState {
    data object Loading: EditPasswordUiState
    data object Success: EditPasswordUiState
    data class Error(val message: String): EditPasswordUiState
}