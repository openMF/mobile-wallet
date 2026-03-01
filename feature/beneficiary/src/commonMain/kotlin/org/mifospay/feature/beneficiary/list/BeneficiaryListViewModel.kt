/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.beneficiary.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import mobile_wallet.feature.beneficiary.generated.resources.Res
import mobile_wallet.feature.beneficiary.generated.resources.delete_beneficiary_subtitle
import mobile_wallet.feature.beneficiary.generated.resources.delete_beneficiary_title
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_deleted
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.beneficiary.BeneficiaryAddEditType

@OptIn(ExperimentalCoroutinesApi::class)
class BeneficiaryListViewModel(
    private val userRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
    private val json: Json,
) : BaseViewModel<BeneficiaryListState, BeneficiaryListEvent, BeneficiaryListAction>(
    initialState = run {
        val clientId = requireNotNull(userRepository.clientId.value)
        val defaultAccount = userRepository.defaultAccountId.value

        BeneficiaryListState(
            clientId = clientId,
            defaultAccountId = defaultAccount,
        )
    },
) {
    val accountState = mutableStateFlow
        .flatMapLatest {
            repository.getBeneficiaryList()
        }
        .mapLatest {
            when (it) {
                is DataState.Loading -> BeneficiaryListState.ViewState.Loading
                is DataState.Error -> BeneficiaryListState.ViewState.Error(it.exception.message.toString())
                is DataState.Success -> {
                    BeneficiaryListState.ViewState.Content(
                        beneficiaries = it.data,
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BeneficiaryListState.ViewState.Loading,
        )

    override fun handleAction(action: BeneficiaryListAction) {
        when (action) {
            is BeneficiaryListAction.AddTPTBeneficiary -> {
                sendEvent(BeneficiaryListEvent.OnAddOrEditTPTBeneficiary(BeneficiaryAddEditType.AddItem()))
            }

            is BeneficiaryListAction.EditBeneficiary -> {
                viewModelScope.launch {
                    val beneficiary = json.encodeToString<Beneficiary>(action.beneficiary)
                    sendEvent(
                        BeneficiaryListEvent.OnAddOrEditTPTBeneficiary(
                            BeneficiaryAddEditType.EditItem(beneficiary),
                        ),
                    )
                }
            }

            is BeneficiaryListAction.DeleteBeneficiary -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = BeneficiaryListState.DialogState.DeleteBeneficiary(
                            title = Res.string.delete_beneficiary_title,
                            message = Res.string.delete_beneficiary_subtitle,
                            onConfirm = {
                                trySendAction(BeneficiaryListAction.Internal.DeleteBeneficiary(action.beneficiaryId))
                            },
                        ),
                    )
                }
            }

            is BeneficiaryListAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is BeneficiaryListAction.Internal.BeneficiaryDeleteResultReceived ->
                handleBeneficiaryDeleteResult(action)

            is BeneficiaryListAction.Internal.DeleteBeneficiary -> handleDeleteBeneficiary(action)
        }
    }

    private fun handleDeleteBeneficiary(action: BeneficiaryListAction.Internal.DeleteBeneficiary) {
        mutableStateFlow.update { it.copy(dialogState = BeneficiaryListState.DialogState.Loading) }

        viewModelScope.launch {
            val result = repository.deleteBeneficiary(action.beneficiaryId)

            sendAction(BeneficiaryListAction.Internal.BeneficiaryDeleteResultReceived(result))
        }
    }

    private fun handleBeneficiaryDeleteResult(action: BeneficiaryListAction.Internal.BeneficiaryDeleteResultReceived) {
        when (action.result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(BeneficiaryListEvent.ShowToast(Res.string.feature_beneficiary_deleted))
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()

                mutableStateFlow.update {
                    it.copy(dialogState = BeneficiaryListState.DialogState.Error(message))
                }
            }

            DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = BeneficiaryListState.DialogState.Loading)
                }
            }
        }
    }
}

data class BeneficiaryListState(
    val clientId: Long,
    val defaultAccountId: Long? = null,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
        data class DeleteBeneficiary(
            val title: StringResource,
            val message: StringResource,
            val onConfirm: () -> Unit,
        ) : DialogState
    }

    sealed interface ViewState {
        val hasFab: Boolean

        val isPullToRefreshEnabled: Boolean

        data object Loading : ViewState {
            override val hasFab: Boolean get() = false
            override val isPullToRefreshEnabled: Boolean get() = false
        }

        data class Error(val message: String) : ViewState {
            override val hasFab: Boolean get() = false
            override val isPullToRefreshEnabled: Boolean get() = false
        }

        data class Content(
            val beneficiaries: List<Beneficiary>,
        ) : ViewState {
            override val hasFab: Boolean get() = true
            override val isPullToRefreshEnabled: Boolean get() = true
        }
    }
}

sealed interface BeneficiaryListEvent {
    data class OnAddOrEditTPTBeneficiary(val type: BeneficiaryAddEditType) : BeneficiaryListEvent

    data class ShowToast(val message: StringResource) : BeneficiaryListEvent
}

sealed interface BeneficiaryListAction {
    data object AddTPTBeneficiary : BeneficiaryListAction
    data class EditBeneficiary(val beneficiary: Beneficiary) : BeneficiaryListAction
    data class DeleteBeneficiary(val beneficiaryId: Long) : BeneficiaryListAction

    data object DismissDialog : BeneficiaryListAction

    sealed interface Internal : BeneficiaryListAction {
        data class DeleteBeneficiary(val beneficiaryId: Long) : Internal
        data class BeneficiaryDeleteResultReceived(val result: DataState<String>) : Internal
    }
}
