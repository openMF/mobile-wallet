/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.StandingInstructionRepository
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.ui.utils.BaseViewModel

private const val KEY_STATE = "sid_details_state"

internal class SIDetailViewModel(
    repository: StandingInstructionRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SIDetailState, SIDEvent, SIDAction>(
    initialState = savedStateHandle[KEY_STATE] ?: SIDetailState(ViewState.Loading),
) {

    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)

        val instructionId = requireNotNull(savedStateHandle.get<Long>("instructionId"))

        repository.getStandingInstruction(instructionId).onEach {
            sendAction(SIDAction.Internal.HandleSIDResult(it))
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: SIDAction) {
        when (action) {
            is SIDAction.NavigateBack -> sendEvent(SIDEvent.OnNavigateBack)

            is SIDAction.Internal.HandleSIDResult -> handleSIDResult(action)
        }
    }

    private fun handleSIDResult(action: SIDAction.Internal.HandleSIDResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error(action.result.message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Content(action.result.data))
                }
            }
        }
    }
}

@Parcelize
internal data class SIDetailState(
    val viewState: ViewState,
) : Parcelable

internal sealed interface ViewState : Parcelable {
    @Parcelize
    data object Loading : ViewState

    @Parcelize
    data class Error(val message: String) : ViewState

    @Parcelize
    data class Content(val data: StandingInstruction) : ViewState
}

internal sealed interface SIDEvent {
    data object OnNavigateBack : SIDEvent
}

internal sealed interface SIDAction {

    data object NavigateBack : SIDAction

    sealed interface Internal : SIDAction {
        data class HandleSIDResult(val result: DataState<StandingInstruction>) : SIDAction
    }
}
