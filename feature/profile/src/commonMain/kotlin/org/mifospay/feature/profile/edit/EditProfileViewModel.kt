/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.common.utils.isValidEmail
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.profile.edit.EditProfileAction.Internal.HandleLoadClientImageResult
import org.mifospay.feature.profile.edit.EditProfileAction.Internal.HandleUpdateClientImageResult
import org.mifospay.feature.profile.edit.EditProfileAction.Internal.LoadClientImage
import org.mifospay.feature.profile.edit.EditProfileAction.Internal.OnUpdateProfileResult
import org.mifospay.feature.profile.edit.EditProfileState.DialogState.Error

private const val KEY = "edit_profile_state"

internal class EditProfileViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditProfileState, EditProfileEvent, EditProfileAction>(
    initialState = savedStateHandle[KEY] ?: run {
        val client = requireNotNull(preferencesRepository.client.value)

        EditProfileState(
            clientId = client.id,
            firstNameInput = client.firstname,
            lastNameInput = client.lastname,
            emailInput = client.emailAddress,
            phoneNumberInput = client.mobileNo,
            externalIdInput = client.externalId,
        )
    },
) {
    init {
//        stateFlow
//            .onEach { savedStateHandle[KEY] = it }
//            .launchIn(viewModelScope)

        trySendAction(LoadClientImage(state.clientId))
    }

    override fun handleAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.FirstNameInputChange -> {
                mutableStateFlow.update {
                    it.copy(firstNameInput = action.firstName)
                }
            }

            is EditProfileAction.LastNameInputChange -> {
                mutableStateFlow.update {
                    it.copy(lastNameInput = action.lastName)
                }
            }

            is EditProfileAction.EmailInputChange -> {
                mutableStateFlow.update {
                    it.copy(emailInput = action.email)
                }
            }

            is EditProfileAction.ExternalIdInputChange -> {
                mutableStateFlow.update {
                    it.copy(externalIdInput = action.externalId)
                }
            }

            is EditProfileAction.PhoneNumberInputChange -> {
                mutableStateFlow.update {
                    it.copy(phoneNumberInput = action.phoneNumber)
                }
            }

            is EditProfileAction.DismissErrorDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is EditProfileAction.NavigateBack -> {
                sendEvent(EditProfileEvent.NavigateBack)
            }

            is EditProfileAction.ProfileImageChange -> handleChangeProfileImage(action)

            is OnUpdateProfileResult -> handleUpdateProfileResult(action)

            is HandleLoadClientImageResult -> handleLoadClientImageResult(action)

            is LoadClientImage -> loadClientImage(action)

            is EditProfileAction.UpdateProfile -> handleUpdateProfile()

            is HandleUpdateClientImageResult -> handleUpdateClientImageResult(action)
        }
    }

    private fun handleLoadClientImageResult(action: HandleLoadClientImageResult) {
        when (action.result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(imageInput = action.result.data)
                }
            }

            is DataState.Error -> {
//                mutableStateFlow.update {
//                    it.copy(dialogState = Error(action.result.exception.message ?: ""))
//                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = EditProfileState.DialogState.Loading)
                }
            }
        }
    }

    private fun loadClientImage(action: LoadClientImage) {
        clientRepository.getClientImage(action.clientId).onEach {
            sendAction(HandleLoadClientImageResult(it))
        }.launchIn(viewModelScope)
    }

    private fun handleChangeProfileImage(action: EditProfileAction.ProfileImageChange) {
        mutableStateFlow.update {
            it.copy(imageInput = action.imageString)
        }
    }

    private fun handleUpdateProfile() = when {
        state.firstNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Please enter client firstname."))
            }
        }

        state.lastNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Please enter client lastname."))
            }
        }

        state.emailInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Please enter your email."))
            }
        }

        !state.emailInput.isValidEmail() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Please enter a valid email."))
            }
        }

        state.phoneNumberInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Please enter your mobile number."))
            }
        }

        state.phoneNumberInput.length < 10 -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Mobile number must be 10 digits long."))
            }
        }

        else -> initiateUpdateProfile()
    }

    private fun initiateUpdateProfile() {
        viewModelScope.launch {
            val result = clientRepository.updateClient(state.clientId, state.updatedClient)

            sendAction(OnUpdateProfileResult(result))
        }
    }

    private fun handleUpdateProfileResult(action: OnUpdateProfileResult) {
        when (action.result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = Error(action.result.exception.message ?: ""))
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = EditProfileState.DialogState.Loading)
                }
            }

            is DataState.Success -> {
                viewModelScope.launch {
                    if (state.imageInput != null) {
                        val result = clientRepository.updateClientImage(
                            state.clientId,
                            state.imageInput!!,
                        )
                        sendAction(HandleUpdateClientImageResult(result))
                    }

                    val result = preferencesRepository.updateClientProfile(state.updatedClient)

                    when (result) {
                        is DataState.Success -> {
                            sendEvent(EditProfileEvent.ShowToast("Profile updated successfully"))
                            trySendAction(EditProfileAction.NavigateBack)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun handleUpdateClientImageResult(action: HandleUpdateClientImageResult) {
        when (action.result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = Error(action.result.exception.message ?: ""))
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = EditProfileState.DialogState.Loading)
                }
            }

            is DataState.Success -> {
                sendEvent(EditProfileEvent.ShowToast("Profile image updated successfully"))
            }
        }
    }
}

@Parcelize
internal data class EditProfileState(
    val clientId: Long,
    val firstNameInput: String,
    val lastNameInput: String,
    val phoneNumberInput: String,
    val emailInput: String,
    val externalIdInput: String,
    val imageInput: String? = null,
    val dialogState: DialogState? = null,
) : Parcelable {
    @IgnoredOnParcel
    internal val updatedClient = UpdatedClient(
        firstname = this.firstNameInput,
        lastname = this.lastNameInput,
        emailAddress = this.emailInput,
        mobileNo = this.phoneNumberInput,
        externalId = this.externalIdInput,
    )

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }
}

sealed interface EditProfileEvent {
    data object NavigateBack : EditProfileEvent
    data class ShowToast(val message: String) : EditProfileEvent
}

sealed interface EditProfileAction {
    data class FirstNameInputChange(val firstName: String) : EditProfileAction
    data class LastNameInputChange(val lastName: String) : EditProfileAction
    data class PhoneNumberInputChange(val phoneNumber: String) : EditProfileAction
    data class EmailInputChange(val email: String) : EditProfileAction
    data class ExternalIdInputChange(val externalId: String) : EditProfileAction

    data class ProfileImageChange(val imageString: String) : EditProfileAction

    data object DismissErrorDialog : EditProfileAction
    data object NavigateBack : EditProfileAction

    data object UpdateProfile : EditProfileAction

    sealed interface Internal : EditProfileAction {
        data class LoadClientImage(val clientId: Long) : Internal
        data class HandleLoadClientImageResult(val result: DataState<String>) : Internal

        data class OnUpdateProfileResult(val result: DataState<String>) : Internal

        data class HandleUpdateClientImageResult(val result: DataState<String>) : Internal
    }
}
