/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.savedcards.details

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.SavedCardRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.savedcards.SavedCard
import org.mifospay.core.ui.utils.BaseViewModel

private const val KEY_STATE = "card_detail_state"

internal class CardDetailViewModel(
    repository: SavedCardRepository,
    userRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<CardDetailState, CardDetailEvent, CardDetailAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val clientId = requireNotNull(userRepository.clientId.value)
        val cardId = requireNotNull(savedStateHandle.get<Long>("cardId"))

        CardDetailState(
            clientId = clientId,
            cardId = cardId,
        )
    },
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val cartDetailState = repository.getSavedCard(state.clientId, state.cardId)
        .mapLatest { result ->
            when (result) {
                is DataState.Loading -> ViewState.Loading
                is DataState.Error -> ViewState.Error(result.exception.message.toString())
                is DataState.Success -> ViewState.Content(result.data)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewState.Loading,
        )

//    init {
//        stateFlow
//            .onEach { savedStateHandle[KEY_STATE] = it }
//            .launchIn(viewModelScope)
//    }

    override fun handleAction(action: CardDetailAction) {
        when (action) {
            CardDetailAction.NavigateBack -> {
                sendEvent(CardDetailEvent.OnNavigateBack)
            }
        }
    }
}

@Parcelize
internal data class CardDetailState(
    val clientId: Long,
    val cardId: Long,
) : Parcelable

internal sealed interface ViewState {
    data object Loading : ViewState

    data class Error(val message: String) : ViewState

    data class Content(val savedCard: SavedCard) : ViewState {
        val selectedColor = Color(savedCard.backgroundColor.toULong())
    }
}

internal sealed interface CardDetailEvent {
    data object OnNavigateBack : CardDetailEvent
}

internal sealed interface CardDetailAction {
    data object NavigateBack : CardDetailAction
}
