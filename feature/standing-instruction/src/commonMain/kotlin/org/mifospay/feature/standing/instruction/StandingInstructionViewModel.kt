/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mobile_wallet.feature.standing_instruction.generated.resources.Res
import mobile_wallet.feature.standing_instruction.generated.resources.feature_standing_instruction_delete
import mobile_wallet.feature.standing_instruction.generated.resources.feature_standing_instruction_delete_message
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.repository.StandingInstructionRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.standing.instruction.createOrUpdate.SIAddEditType

class StandingInstructionViewModel(
    private val repository: UserPreferencesRepository,
    private val siRepository: StandingInstructionRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SIUiState, SIEvent, SIAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: run {
        val clientId = requireNotNull(repository.clientId.value)

        SIUiState(clientId = clientId)
    },
) {

    companion object {
        private const val KEY_STATE = "standing_instruction_state"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    // Phase-5 Batch-3 cutover — switched from the transitional
    // `getAllStandingInstructions(clientId)` shim to the store-backed
    // `getAllStandingInstructionsScreen(clientId, scope)` (GOAL D13,
    // `AppStoreRegistry.StandingInstruction`, offline-first via
    // `wallet_standing_instructions` Room SoT, CACHE_FIRST_SWR band). The
    // stateIn(...) 5-second WhileSubscribed keep-alive is preserved so a
    // Compose-tab-away / return round-trip still hits the same live stream
    // rather than resubscribing.
    val viewState = siRepository.getAllStandingInstructionsScreen(
        clientId = state.clientId,
        scope = viewModelScope,
    ).mapLatest { result ->
        // Fold ScreenState → the existing 4-branch SIViewState. Repo emits
        // ScreenState.Empty when the underlying list is empty, so we route it
        // to the feature's real Empty branch (add-SI CTA). NoNetwork /
        // Unauthenticated → Error until Phase-4 differentiates.
        when (result) {
            is ScreenState.Loading -> SIViewState.Loading

            is ScreenState.Empty -> SIViewState.Empty

            is ScreenState.Content -> SIViewState.Content(result.data)

            is ScreenState.Error -> SIViewState.Error(result.error.message.toString())

            is ScreenState.NoNetwork ->
                SIViewState.Error("No network. Please check your connection.")

            is ScreenState.Unauthenticated ->
                SIViewState.Error("Session expired. Please log in again.")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SIViewState.Loading,
    )

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: SIAction) {
        when (action) {
            is SIAction.AddNewSI -> {
                sendEvent(SIEvent.OnAddEditSI(SIAddEditType.AddItem))
            }

            is SIAction.EditSIDetails -> {
                sendEvent(SIEvent.OnAddEditSI(SIAddEditType.EditItem(action.siId)))
            }

            is SIAction.ViewSIDetails -> {
                sendEvent(SIEvent.OnNavigateToSIDetails(action.siId))
            }

            is SIAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is SIAction.OnDeleteSI -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = SIUiState.DialogState.DeleteSI(
                            title = Res.string.feature_standing_instruction_delete,
                            message = Res.string.feature_standing_instruction_delete_message,
                            onConfirm = {
                                trySendAction(SIAction.Internal.DeleteSI(action.siId))
                            },
                        ),
                    )
                }
            }

            is SIAction.Internal.DeleteSI -> deleteSI(action)

            is SIAction.Internal.HandleSIDeleteResult -> handleDeleteResult(action)
        }
    }

    private fun deleteSI(action: SIAction.Internal.DeleteSI) {
        mutableStateFlow.update {
            it.copy(dialogState = SIUiState.DialogState.Loading)
        }

        viewModelScope.launch {
            val result = siRepository.deleteStandingInstruction(action.siId)

            sendAction(SIAction.Internal.HandleSIDeleteResult(result))
        }
    }

    private fun handleDeleteResult(action: SIAction.Internal.HandleSIDeleteResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = SIUiState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(dialogState = SIUiState.DialogState.Error(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(SIEvent.ShowToast(action.result.data))
            }
        }
    }
}

@Serializable
data class SIUiState(
    val clientId: Long,
    @Transient
    val dialogState: DialogState? = null,
) {

    sealed interface DialogState {
        data object Loading : DialogState

        data class Error(val message: String) : DialogState

        data class DeleteSI(
            val title: StringResource,
            val message: StringResource,
            val onConfirm: () -> Unit,
        ) : DialogState
    }
}

sealed interface SIViewState {
    val hasFab: Boolean
    val isPullRefreshEnabled: Boolean

    data object Loading : SIViewState {
        override val hasFab: Boolean get() = false
        override val isPullRefreshEnabled: Boolean get() = false
    }

    data class Error(val message: String) : SIViewState {
        override val hasFab: Boolean get() = false
        override val isPullRefreshEnabled: Boolean get() = false
    }

    data object Empty : SIViewState {
        val title: String get() = "No Standing Instructions"
        val message: String get() = "No standing instructions found for this client."
        val btnText: String get() = "Add New SI"
        val btnIcon: ImageVector get() = MifosIcons.Add

        override val hasFab: Boolean get() = false
        override val isPullRefreshEnabled: Boolean get() = true
    }

    data class Content(val list: List<StandingInstruction>) : SIViewState {
        override val hasFab: Boolean get() = true
        override val isPullRefreshEnabled: Boolean get() = true
    }
}

sealed interface SIEvent {
    data class ShowToast(val message: String) : SIEvent
    data class OnNavigateToSIDetails(val siId: Long) : SIEvent
    data class OnAddEditSI(val type: SIAddEditType) : SIEvent
}

sealed interface SIAction {
    data object AddNewSI : SIAction
    data class OnDeleteSI(val siId: Long) : SIAction
    data class EditSIDetails(val siId: Long) : SIAction
    data class ViewSIDetails(val siId: Long) : SIAction

    data object DismissDialog : SIAction

    sealed interface Internal : SIAction {
        data class DeleteSI(val siId: Long) : Internal
        data class HandleSIDeleteResult(val result: DataState<String>) : Internal
    }
}
