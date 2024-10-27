/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.savedcards

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
import mobile_wallet.feature.savedcards.generated.resources.Res
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_confirm_delete_card
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_delete_card
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.SavedCardRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.savedcards.SavedCard
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.savedcards.CardAction.Internal.HandleCardDeleteResult
import org.mifospay.feature.savedcards.createOrUpdate.CardAddEditType

private const val KEY_STATE = "saved_card_state"

class CardsScreenViewModel(
    private val repository: SavedCardRepository,
    userRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<CardState, CardEvent, CardAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val clientId = requireNotNull(userRepository.clientId.value)

        CardState(clientId = clientId)
    },
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val cardState = repository.getSavedCards(state.clientId)
        .mapLatest { result ->
            when (result) {
                is DataState.Loading -> ViewState.Loading
                is DataState.Error -> ViewState.Error(result.exception.message.toString())
                is DataState.Success -> {
                    if (result.data.isEmpty()) ViewState.Empty else ViewState.Content(result.data)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewState.Loading,
        )

    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: CardAction) {
        when (action) {
            is CardAction.AddNewCard -> {
                sendEvent(CardEvent.OnNavigateToAddEdit(CardAddEditType.AddItem))
            }

            is CardAction.ViewCardDetails -> {
                sendEvent(CardEvent.OnNavigateToCardDetails(action.cardId))
            }

            is CardAction.EditCardDetails -> {
                sendEvent(CardEvent.OnNavigateToAddEdit(CardAddEditType.EditItem(action.cardId)))
            }

            is CardAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is CardAction.DeleteCardClicked -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = CardState.DialogState.DeleteCard(
                            title = Res.string.feature_savedcards_delete_card,
                            message = Res.string.feature_savedcards_confirm_delete_card,
                            onConfirm = {
                                trySendAction(CardAction.Internal.DeleteCard(action.cardId))
                            },
                        ),
                    )
                }
            }

            is CardAction.Internal.DeleteCard -> deleteCard(action)

            is HandleCardDeleteResult -> handleCardDeleteResult(action)
        }
    }

    private fun deleteCard(action: CardAction.Internal.DeleteCard) {
        mutableStateFlow.update {
            it.copy(dialogState = CardState.DialogState.Loading)
        }

        viewModelScope.launch {
            val result = repository.deleteCard(state.clientId, action.cardId)

            sendAction(HandleCardDeleteResult(result))
        }
    }

    private fun handleCardDeleteResult(action: HandleCardDeleteResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = CardState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(dialogState = CardState.DialogState.Error(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(CardEvent.ShowToast(action.result.data))
            }
        }
    }
}

@Parcelize
data class CardState(
    val clientId: Long,
    @IgnoredOnParcel
    val dialogState: DialogState? = null,
) : Parcelable {

    sealed interface DialogState {
        data object Loading : DialogState

        data class Error(val message: String) : DialogState

        data class DeleteCard(
            val title: StringResource,
            val message: StringResource,
            val onConfirm: () -> Unit,
        ) : DialogState
    }
}

sealed interface ViewState {
    val hasFab: Boolean
    val isPullRefreshEnabled: Boolean

    data object Loading : ViewState {
        override val hasFab: Boolean get() = false
        override val isPullRefreshEnabled: Boolean get() = false
    }

    data class Error(val message: String) : ViewState {
        override val hasFab: Boolean get() = false
        override val isPullRefreshEnabled: Boolean get() = false
    }

    data object Empty : ViewState {
        val title: String get() = "No Saved Cards"
        val message: String get() = "No saved cards found, click the button below to add a new card"
        val btnText: String get() = "Add New Card"
        val btnIcon: ImageVector get() = MifosIcons.Add

        override val hasFab: Boolean get() = false
        override val isPullRefreshEnabled: Boolean get() = true
    }

    data class Content(
        val cards: List<SavedCard>,
    ) : ViewState {
        override val hasFab: Boolean get() = true
        override val isPullRefreshEnabled: Boolean get() = true
    }
}

sealed interface CardEvent {
    data class OnNavigateToCardDetails(val cardId: Long) : CardEvent
    data class OnNavigateToAddEdit(val type: CardAddEditType) : CardEvent
    data class ShowToast(val message: String) : CardEvent
}

sealed interface CardAction {
    data class ViewCardDetails(val cardId: Long) : CardAction
    data class EditCardDetails(val cardId: Long) : CardAction
    data class DeleteCardClicked(val cardId: Long) : CardAction
    data object DismissDialog : CardAction
    data object AddNewCard : CardAction

    sealed interface Internal : CardAction {
        data class DeleteCard(val cardId: Long) : Internal
        data class HandleCardDeleteResult(val result: DataState<String>) : Internal
    }
}
