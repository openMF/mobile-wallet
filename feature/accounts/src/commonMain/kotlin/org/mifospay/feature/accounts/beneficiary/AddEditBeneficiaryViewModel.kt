/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts.beneficiary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.LocalAssetRepository
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.accounts.beneficiary.AEBAction.Internal.HandleBeneficiaryAddEditResult
import org.mifospay.feature.accounts.beneficiary.AEBState.DialogState.Error

private const val KEY = "AddEditBeneficiaryViewModel"

internal class AddEditBeneficiaryViewModel(
    private val localAssetRepository: LocalAssetRepository,
    private val repository: SelfServiceRepository,
    private val json: Json,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AEBState, AEBEvent, AEBAction>(
    initialState = savedStateHandle[KEY] ?: run {
        when (val addEditType = BeneficiaryAddEditArgs(savedStateHandle).addEditType) {
            is BeneficiaryAddEditType.AddItem -> {
                AEBState(
                    name = "",
                    accountNumber = "",
                    transferLimit = 0,
                    addEditType = addEditType,
                )
            }

            is BeneficiaryAddEditType.EditItem -> {
                val beneficiary = json.decodeFromString(
                    Beneficiary.serializer(),
                    addEditType.beneficiary,
                )

                AEBState(
                    name = beneficiary.name,
                    accountNumber = beneficiary.accountNumber,
                    transferLimit = beneficiary.transferLimit,
                    officeName = beneficiary.officeName,
                    beneficiaryId = beneficiary.id,
                    addEditType = addEditType,
                )
            }
        }
    },
) {
    val filteredLocalList = localAssetRepository.localeList.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5_000),
    )

//    init {
//        stateFlow
//            .onEach { savedStateHandle[KEY] = it }
//            .launchIn(viewModelScope)
//    }

    override fun handleAction(action: AEBAction) {
        when (action) {
            is AEBAction.ChangeName -> {
                mutableStateFlow.update {
                    it.copy(name = action.name)
                }
            }

            is AEBAction.ChangeTransferLimit -> {
                mutableStateFlow.update {
                    it.copy(transferLimit = action.transferLimit.toIntOrNull() ?: 0)
                }
            }

            is AEBAction.ChangeAccountNumber -> {
                mutableStateFlow.update {
                    it.copy(accountNumber = action.accountNumber)
                }
            }

            is AEBAction.ChangeAccountType -> {
                mutableStateFlow.update {
                    it.copy(accountType = action.accountType)
                }
            }

            is AEBAction.ChangeLocale -> {
                mutableStateFlow.update {
                    it.copy(locale = action.locale)
                }
            }

            is AEBAction.ChangeOfficeName -> {
                mutableStateFlow.update {
                    it.copy(officeName = action.officeName)
                }
            }

            AEBAction.NavigateBack -> {
                sendEvent(AEBEvent.NavigateBack)
            }

            AEBAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            AEBAction.SaveBeneficiary -> initiateSaveBeneficiary()

            is HandleBeneficiaryAddEditResult -> handleBeneficiaryAddEditResult(action)
        }
    }

    private fun initiateSaveBeneficiary() = when {
        state.name.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Beneficiary Name cannot be empty"))
            }
        }

        state.accountNumber.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Account number cannot be empty"))
            }
        }

        state.transferLimit <= 0 -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Transfer limit should be greater than 0"))
            }
        }

        state.locale.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Select a locale"))
            }
        }

        state.officeName.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Office name cannot be empty"))
            }
        }

        state.accountType == 0 -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error("Select an account type"))
            }
        }

        else -> handleSaveBeneficiary()
    }

    private fun handleSaveBeneficiary() {
        mutableStateFlow.update {
            it.copy(dialogState = AEBState.DialogState.Loading)
        }

        when (state.addEditType) {
            is BeneficiaryAddEditType.AddItem -> {
                val payload = BeneficiaryPayload(
                    name = state.name,
                    accountNumber = state.accountNumber,
                    transferLimit = state.transferLimit,
                    locale = state.locale,
                    officeName = state.officeName,
                    accountType = state.accountType,
                )

                viewModelScope.launch {
                    val result = repository.createBeneficiary(payload)

                    sendAction(HandleBeneficiaryAddEditResult(result))
                }
            }

            is BeneficiaryAddEditType.EditItem -> {
                val payload = BeneficiaryUpdatePayload(
                    name = state.name,
                    transferLimit = state.transferLimit,
                )

                viewModelScope.launch {
                    val beneficiaryId = state.beneficiaryId ?: return@launch

                    val result = repository.updateBeneficiary(beneficiaryId, payload)

                    sendAction(HandleBeneficiaryAddEditResult(result))
                }
            }
        }
    }

    private fun handleBeneficiaryAddEditResult(action: HandleBeneficiaryAddEditResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AEBState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = Error(action.result.exception.toString()))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                sendEvent(AEBEvent.ShowToast(action.result.data))
                sendEvent(AEBEvent.NavigateBack)
            }
        }
    }
}

@Parcelize
internal data class AEBState(
    val addEditType: BeneficiaryAddEditType,
    val name: String,
    val accountNumber: String,
    val transferLimit: Int,
    val locale: String = "en_US",
    val officeName: String = OFFICE_NAME,
    val accountType: Int = SAVINGS_ACC_ID,
    val beneficiaryId: Long? = null,
    val dialogState: DialogState? = null,
) : Parcelable {
    private val isAddItemMode: Boolean
        get() = addEditType is BeneficiaryAddEditType.AddItem

    val btnText: String
        get() = if (isAddItemMode) "Save" else "Update"

    val title: String
        get() = if (isAddItemMode) "Add Beneficiary" else "Update Beneficiary"

    val accountTypeName: String
        get() = if (accountType == SAVINGS_ACC_ID) "WALLET" else "Other"

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }

    companion object {
        const val SAVINGS_ACC_ID = 2
        const val OFFICE_NAME = "Head Office"
    }
}

internal sealed interface AEBEvent {
    data object NavigateBack : AEBEvent
    data class ShowToast(val message: String) : AEBEvent
}

internal sealed interface AEBAction {
    data class ChangeLocale(val locale: String) : AEBAction
    data class ChangeName(val name: String) : AEBAction
    data class ChangeOfficeName(val officeName: String) : AEBAction
    data class ChangeAccountNumber(val accountNumber: String) : AEBAction
    data class ChangeAccountType(val accountType: Int) : AEBAction
    data class ChangeTransferLimit(val transferLimit: String) : AEBAction

    data object DismissDialog : AEBAction
    data object NavigateBack : AEBAction
    data object SaveBeneficiary : AEBAction

    sealed interface Internal : AEBAction {
        data class HandleBeneficiaryAddEditResult(val result: DataState<String>) : Internal
    }
}
