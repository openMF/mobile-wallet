/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.common.takeUntilResultSuccess
import org.mifospay.core.data.repository.KycLevelRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.kyc.KYCLevel1Details
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.kyc.KycLevel1Action.Internal.HandleLevel1Result
import org.mifospay.feature.kyc.KycLevel1Action.Internal.KycLevel1DetailsResult
import org.mifospay.feature.kyc.KycLevel1State.DialogState.Error

private const val KEY_STATE = "kyc_level_1_state"

internal class KYCLevel1ViewModel(
    private val kycLevelRepository: KycLevelRepository,
    private val repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<KycLevel1State, KycLevel1Event, KycLevel1Action>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val clientId = requireNotNull(repository.clientId.value)

        KycLevel1State(clientId = clientId)
    },
) {

    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)

        kycLevelRepository.fetchKYCLevel1Details(state.clientId)
            .takeUntilResultSuccess()
            .onEach {
                sendAction(HandleLevel1Result(it))
            }.launchIn(viewModelScope)
    }

    override fun handleAction(action: KycLevel1Action) {
        when (action) {
            is KycLevel1Action.FirstNameChanged -> {
                mutableStateFlow.update {
                    it.copy(firstNameInput = action.firstName)
                }
            }

            is KycLevel1Action.LastNameChanged -> {
                mutableStateFlow.update {
                    it.copy(lastNameInput = action.lastName)
                }
            }

            is KycLevel1Action.MobileNoChanged -> {
                mutableStateFlow.update {
                    it.copy(mobileNoInput = action.mobileNo)
                }
            }

            is KycLevel1Action.AddressLine1Changed -> {
                mutableStateFlow.update {
                    it.copy(addressLine1Input = action.addressLine1)
                }
            }

            is KycLevel1Action.AddressLine2Changed -> {
                mutableStateFlow.update {
                    it.copy(addressLine2Input = action.addressLine2)
                }
            }

            is KycLevel1Action.DobChanged -> {
                val formattedDate = DateHelper.getDateAsStringFromLong(action.dob)

                mutableStateFlow.update {
                    it.copy(dobInput = formattedDate)
                }
            }

            KycLevel1Action.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            KycLevel1Action.NavigateToKycLevel2 -> {
                sendEvent(KycLevel1Event.NavigateToKycLevel2)
            }

            is KycLevel1Action.NavigateBack -> {
                sendEvent(KycLevel1Event.OnNavigateBack)
            }

            KycLevel1Action.SubmitClicked -> initiateKycLevel1Submission()

            is KycLevel1DetailsResult -> handleKycLevel1DetailsResult(action)

            is HandleLevel1Result -> handleLevel1Result(action)
        }
    }

    private fun initiateKycLevel1Submission() = when {
        state.firstNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("First name is required"))
            }
        }

        state.lastNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Last name is required"))
            }
        }

        state.mobileNoInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Mobile number is required"))
            }
        }

        state.mobileNoInput.length != 10 -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Mobile number should be 10 digits"))
            }
        }

        state.addressLine1Input.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Address line 1 is required"))
            }
        }

        state.addressLine2Input.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Address line 2 is required"))
            }
        }

        state.dobInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Date of birth is required"))
            }
        }

        else -> submitKycLevel1Details()
    }

    private fun submitKycLevel1Details() {
        mutableStateFlow.update {
            it.copy(dialogState = KycLevel1State.DialogState.Loading)
        }

        viewModelScope.launch {
            val result = if (state.doesExist) {
                kycLevelRepository.updateKYCLevel1Details(state.clientId, state.details)
            } else {
                kycLevelRepository.addKYCLevel1Details(state.clientId, state.details)
            }

            sendAction(KycLevel1DetailsResult(result))
        }
    }

    private fun handleKycLevel1DetailsResult(action: KycLevel1DetailsResult) {
        when (action.result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                sendEvent(KycLevel1Event.ShowToast(action.result.data))
                sendEvent(KycLevel1Event.NavigateToKycLevel2)
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(dialogState = Error(message))
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = KycLevel1State.DialogState.Loading)
                }
            }
        }
    }

    private fun handleLevel1Result(action: HandleLevel1Result) {
        when (action.result) {
            is DataState.Success -> {
                action.result.data?.let { data ->
                    mutableStateFlow.update {
                        it.copy(
                            firstNameInput = data.firstName,
                            lastNameInput = data.lastName,
                            addressLine1Input = data.addressLine1,
                            addressLine2Input = data.addressLine2,
                            mobileNoInput = data.mobileNo,
                            dobInput = data.dob,
                            currentLevelInput = data.currentLevel,
                            doesExist = true,
                            dialogState = null,
                        )
                    }
                } ?: run {
                    mutableStateFlow.update {
                        it.copy(dialogState = null)
                    }
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = KycLevel1State.DialogState.Loading)
                }
            }

            else -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
        }
    }
}

@Parcelize
internal data class KycLevel1State(
    val clientId: Long,
    val firstNameInput: String = "",
    val lastNameInput: String = "",
    val addressLine1Input: String = "",
    val addressLine2Input: String = "",
    val mobileNoInput: String = "",
    val dobInput: String = "",
    val currentLevelInput: String = KycLevel.KYC_LEVEL_1.name,
    val doesExist: Boolean = false,
    val dialogState: DialogState? = null,
) : Parcelable {
    @IgnoredOnParcel
    val title: String
        get() = if (doesExist) "Update Basic Details" else "Enter Basic Details"

    @IgnoredOnParcel
    val submitButtonText: String
        get() = if (doesExist) "Update" else "Submit"

    @IgnoredOnParcel
    val initialDate = Clock.System.now().toEpochMilliseconds()

    @IgnoredOnParcel
    val details = KYCLevel1Details(
        firstName = firstNameInput,
        lastName = lastNameInput,
        addressLine1 = addressLine1Input,
        addressLine2 = addressLine2Input,
        mobileNo = mobileNoInput,
        dob = dobInput,
        currentLevel = currentLevelInput,
    )

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }
}

internal sealed interface KycLevel1Event {
    data object NavigateToKycLevel2 : KycLevel1Event
    data object OnNavigateBack : KycLevel1Event
    data class ShowToast(val message: String) : KycLevel1Event
}

internal sealed interface KycLevel1Action {
    data class FirstNameChanged(val firstName: String) : KycLevel1Action
    data class LastNameChanged(val lastName: String) : KycLevel1Action
    data class AddressLine1Changed(val addressLine1: String) : KycLevel1Action
    data class AddressLine2Changed(val addressLine2: String) : KycLevel1Action
    data class MobileNoChanged(val mobileNo: String) : KycLevel1Action
    data class DobChanged(val dob: Long) : KycLevel1Action

    data object SubmitClicked : KycLevel1Action
    data object DismissDialog : KycLevel1Action
    data object NavigateBack : KycLevel1Action
    data object NavigateToKycLevel2 : KycLevel1Action

    sealed interface Internal : KycLevel1Action {
        data class HandleLevel1Result(val result: DataState<KYCLevel1Details?>) : Internal
        data class KycLevel1DetailsResult(val result: DataState<String>) : Internal
    }
}
