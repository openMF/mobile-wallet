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
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mobile_wallet.feature.profile.generated.resources.Res
import mobile_wallet.feature.profile.generated.resources.feature_profile_error_empty_email
import mobile_wallet.feature.profile.generated.resources.feature_profile_error_empty_firstname
import mobile_wallet.feature.profile.generated.resources.feature_profile_error_empty_lastname
import mobile_wallet.feature.profile.generated.resources.feature_profile_error_empty_phone
import mobile_wallet.feature.profile.generated.resources.feature_profile_error_invalid_email
import mobile_wallet.feature.profile.generated.resources.feature_profile_error_invalid_phone_length
import mobile_wallet.feature.profile.generated.resources.feature_profile_profile_image_updated_successfully
import mobile_wallet.feature.profile.generated.resources.feature_profile_profile_updated_successfully
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
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

internal class EditProfileViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditProfileState, EditProfileEvent, EditProfileAction>(
    initialState = savedStateHandle.getSerialized(KEY) ?: run {
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

    companion object {
        private const val KEY = "edit_profile_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY, value = it) }
            .launchIn(viewModelScope)

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

            is EditProfileAction.PickProfileImage -> handlePickProfileImage(action)

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
                    it.copy(profileImage = action.result.data.encodeToByteArray())
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = Error.StringMessage(action.result.exception.message ?: ""))
                }
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

    private fun handlePickProfileImage(action: EditProfileAction.PickProfileImage) {
        viewModelScope.launch {
            val file = FileKit.openFilePicker(FileKitType.Image)
            file?.let {
                mutableStateFlow.update { state ->
                    state.copy(profileImage = it.readBytes())
                }
            }
        }
    }

    private fun handleUpdateProfile() = when {
        state.firstNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_profile_error_empty_firstname))
            }
        }

        state.lastNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_profile_error_empty_lastname))
            }
        }

        state.emailInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_profile_error_empty_email))
            }
        }

        !state.emailInput.isValidEmail() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_profile_error_invalid_email))
            }
        }

        state.phoneNumberInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_profile_error_empty_phone))
            }
        }

        state.phoneNumberInput.length < 10 -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_profile_error_invalid_phone_length))
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
                    it.copy(dialogState = Error.StringMessage(action.result.exception.message ?: ""))
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = EditProfileState.DialogState.Loading)
                }
            }

            is DataState.Success -> {
                viewModelScope.launch {
                    if (state.profileImage != null) {
                        val result = clientRepository.updateClientImage(
                            state.clientId,
                            state.profileImage!!.decodeToString(),
                        )
                        sendAction(HandleUpdateClientImageResult(result))
                    }

                    val result = preferencesRepository.updateClientProfile(state.updatedClient)

                    when (result) {
                        is DataState.Success -> {
                            sendEvent(EditProfileEvent.ShowToast(Res.string.feature_profile_profile_updated_successfully))
                            sendEvent(EditProfileEvent.NavigateBack)
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
                    it.copy(dialogState = Error.StringMessage(action.result.exception.message ?: ""))
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = EditProfileState.DialogState.Loading)
                }
            }

            is DataState.Success -> {
                sendEvent(EditProfileEvent.ShowToast(Res.string.feature_profile_profile_image_updated_successfully))
            }
        }
    }
}

@Serializable
internal data class EditProfileState(
    val clientId: Long,
    val firstNameInput: String,
    val lastNameInput: String,
    val phoneNumberInput: String,
    val emailInput: String,
    val externalIdInput: String,
    val profileImage: ByteArray? = null,
    val dialogState: DialogState? = null,
) {
    @Transient
    internal val updatedClient = UpdatedClient(
        firstname = this.firstNameInput,
        lastname = this.lastNameInput,
        emailAddress = this.emailInput,
        mobileNo = this.phoneNumberInput,
        externalId = this.externalIdInput,
    )

    @Serializable
    sealed interface DialogState {
        data object Loading : DialogState

        sealed interface Error : DialogState {
            data class StringMessage(val message: String) : Error
            data class ResourceMessage(val message: StringResource) : Error
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EditProfileState

        if (clientId != other.clientId) return false
        if (firstNameInput != other.firstNameInput) return false
        if (lastNameInput != other.lastNameInput) return false
        if (phoneNumberInput != other.phoneNumberInput) return false
        if (emailInput != other.emailInput) return false
        if (externalIdInput != other.externalIdInput) return false
        if (!profileImage.contentEquals(other.profileImage)) return false
        if (dialogState != other.dialogState) return false
        if (updatedClient != other.updatedClient) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clientId.hashCode()
        result = 31 * result + firstNameInput.hashCode()
        result = 31 * result + lastNameInput.hashCode()
        result = 31 * result + phoneNumberInput.hashCode()
        result = 31 * result + emailInput.hashCode()
        result = 31 * result + externalIdInput.hashCode()
        result = 31 * result + (profileImage?.contentHashCode() ?: 0)
        result = 31 * result + (dialogState?.hashCode() ?: 0)
        result = 31 * result + updatedClient.hashCode()
        return result
    }
}

sealed interface EditProfileEvent {
    data object NavigateBack : EditProfileEvent
    data class ShowToast(val message: StringResource) : EditProfileEvent
}

sealed interface EditProfileAction {
    data class FirstNameInputChange(val firstName: String) : EditProfileAction
    data class LastNameInputChange(val lastName: String) : EditProfileAction
    data class PhoneNumberInputChange(val phoneNumber: String) : EditProfileAction
    data class EmailInputChange(val email: String) : EditProfileAction
    data class ExternalIdInputChange(val externalId: String) : EditProfileAction

    data object DismissErrorDialog : EditProfileAction
    data object NavigateBack : EditProfileAction

    data object UpdateProfile : EditProfileAction
    data object PickProfileImage : EditProfileAction

    sealed interface Internal : EditProfileAction {
        data class LoadClientImage(val clientId: Long) : Internal
        data class HandleLoadClientImageResult(val result: DataState<String>) : Internal

        data class OnUpdateProfileResult(val result: DataState<String>) : Internal

        data class HandleUpdateClientImageResult(val result: DataState<String>) : Internal
    }
}
