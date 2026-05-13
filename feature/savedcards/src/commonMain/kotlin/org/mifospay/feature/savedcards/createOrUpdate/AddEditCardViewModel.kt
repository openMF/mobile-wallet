/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.savedcards.createOrUpdate

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.repository.SavedCardRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.savedcards.CardPayload
import org.mifospay.core.model.savedcards.SavedCard
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.savedcards.createOrUpdate.AECardAction.Internal.HandleAddEditCardResult
import org.mifospay.feature.savedcards.createOrUpdate.AECardAction.Internal.HandleCardResult
import org.mifospay.feature.savedcards.utils.CardType
import org.mifospay.feature.savedcards.utils.CreditCardUtils.detectCardType
import org.mifospay.feature.savedcards.utils.CreditCardUtils.isValidCVV
import org.mifospay.feature.savedcards.utils.CreditCardUtils.isValidCreditCardNumber
import org.mifospay.feature.savedcards.utils.CreditCardUtils.isValidExpiryDate
import org.mifospay.feature.savedcards.utils.creditCardColors

internal class AddEditCardViewModel(
    private val repository: SavedCardRepository,
    userRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AECardState, AECardEvent, AECardAction>(
    initialState = savedStateHandle.getSerialized(ADD_EDIT_CARD_STATE_KEY) ?: run {
        val clientId = requireNotNull(userRepository.clientId.value)
        val type = CardAddEditArgs(savedStateHandle).cardAddEditType

        AECardState(
            clientId = clientId,
            type = type,
        )
    },
) {

    companion object {
        private const val ADD_EDIT_CARD_STATE_KEY = "add_edit_card_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = ADD_EDIT_CARD_STATE_KEY, value = it) }
            .launchIn(viewModelScope)

        val editType = state.type as? CardAddEditType.EditItem
        if (editType != null) {
            repository.getSavedCard(state.clientId, editType.savedCardId).onEach {
                sendAction(HandleCardResult(it))
            }.launchIn(viewModelScope)
        }
    }

    override fun handleAction(action: AECardAction) {
        when (action) {
            is AECardAction.FirstNameChanged -> {
                mutableStateFlow.update {
                    it.copy(firstName = action.firstName.uppercase())
                }
            }

            is AECardAction.LastNameChanged -> {
                mutableStateFlow.update {
                    it.copy(lastName = action.lastName.uppercase())
                }
            }

            is AECardAction.CardNumberChanged -> {
                mutableStateFlow.update {
                    it.copy(cardNumber = action.cardNumber)
                }
            }

            is AECardAction.CVVChanged -> {
                mutableStateFlow.update {
                    it.copy(cvv = action.cvv)
                }
            }

            is AECardAction.ExpiryDateChanged -> {
                mutableStateFlow.update {
                    it.copy(expiryDate = action.expiryDate)
                }
            }

            is AECardAction.BackgroundColorChanged -> {
                mutableStateFlow.update {
                    it.copy(backgroundColor = action.backgroundColor)
                }
            }

            AECardAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            AECardAction.NavigateBack -> {
                sendEvent(AECardEvent.OnNavigateBack)
            }

            AECardAction.SaveCard -> initiateSaveCard()

            is HandleAddEditCardResult -> handleAddEditCardResult(action)

            is HandleCardResult -> handleCardResult(action)
        }
    }

    private fun initiateSaveCard() = when {
        state.firstName.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("First name is required"))
            }
        }

        state.lastName.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("Last name is required"))
            }
        }

        state.cardNumber.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("Card number is required"))
            }
        }

        state.cardNumber.isValidCreditCardNumber().not() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("Invalid card number"))
            }
        }

        state.expiryDate.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("Expiry date is required"))
            }
        }

        state.expiryDateFormatted.isValidExpiryDate().not() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("Invalid expiry date"))
            }
        }

        state.cvv.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("CVV is required"))
            }
        }

        state.cvv.isValidCVV(state.cardType).not() -> {
            mutableStateFlow.update {
                it.copy(dialogState = AECardState.DialogState.Error("Invalid CVV"))
            }
        }

        else -> initiateAddOrEditCard()
    }

    private fun initiateAddOrEditCard() {
        mutableStateFlow.update {
            it.copy(dialogState = AECardState.DialogState.Loading)
        }

        viewModelScope.launch {
            when (val type = state.type) {
                is CardAddEditType.AddItem -> {
                    val result = repository.addSavedCard(state.clientId, state.cardPayload)

                    sendAction(HandleAddEditCardResult(result))
                }

                is CardAddEditType.EditItem -> {
                    val result = repository.updateCard(
                        clientId = state.clientId,
                        cardId = type.savedCardId,
                        card = state.cardPayload,
                    )

                    sendAction(HandleAddEditCardResult(result))
                }
            }
        }
    }

    private fun handleAddEditCardResult(action: HandleAddEditCardResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AECardState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()

                mutableStateFlow.update {
                    it.copy(dialogState = AECardState.DialogState.Error(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(AECardEvent.ShowToast("Card saved successfully"))
                sendEvent(AECardEvent.OnNavigateBack)
            }
        }
    }

    private fun handleCardResult(action: HandleCardResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AECardState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()

                mutableStateFlow.update {
                    it.copy(dialogState = AECardState.DialogState.Error(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        firstName = action.result.data.firstName,
                        lastName = action.result.data.lastName,
                        cardNumber = action.result.data.cardNumber,
                        cvv = action.result.data.cvv,
                        expiryDate = action.result.data.expiryDate,
                        backgroundColor = action.result.data.backgroundColor,
                        dialogState = null,
                    )
                }
            }
        }
    }
}

@Serializable
internal data class AECardState(
    val clientId: Long,
    val type: CardAddEditType,
    val firstName: String = "",
    val lastName: String = "",
    val cardNumber: String = "",
    val cvv: String = "",
    val expiryDate: String = "",
    val backgroundColor: String = creditCardColors.first().value.toString(),
    @Transient
    val dialogState: DialogState? = null,
) {

    val isAddMode: Boolean
        get() = type == CardAddEditType.AddItem

    val title: String get() = if (isAddMode) "Add New Card" else "Update Card Details"

    val buttonText: String get() = if (isAddMode) "Add New Card" else "Update"

    val cardType: CardType get() = cardNumber.detectCardType()

    val fullName: String get() = "$firstName $lastName"

    val expiryDateFormatted: String
        get() = if (expiryDate.length == 4) {
            "${expiryDate.substring(0, 2)}/${expiryDate.substring(2, 4)}"
        } else {
            expiryDate
        }

    val maskedCVV get() = "*".repeat(cardType.cvvLength)

    @Transient
    val backgroundColors = creditCardColors

    @Transient
    val selectedColor = Color(backgroundColor.toULong())

    @Transient
    val cardPayload = CardPayload(
        firstName = firstName,
        lastName = lastName,
        cardNumber = cardNumber,
        cvv = cvv,
        expiryDate = expiryDate,
        backgroundColor = backgroundColor,
    )

    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

internal sealed interface AECardEvent {
    data object OnNavigateBack : AECardEvent
    data class ShowToast(val message: String) : AECardEvent
}

internal sealed interface AECardAction {
    data class FirstNameChanged(val firstName: String) : AECardAction
    data class LastNameChanged(val lastName: String) : AECardAction
    data class CardNumberChanged(val cardNumber: String) : AECardAction
    data class CVVChanged(val cvv: String) : AECardAction
    data class ExpiryDateChanged(val expiryDate: String) : AECardAction
    data class BackgroundColorChanged(val backgroundColor: String) : AECardAction

    data object DismissDialog : AECardAction
    data object SaveCard : AECardAction
    data object NavigateBack : AECardAction

    sealed interface Internal : AECardAction {
        data class HandleCardResult(val result: DataState<SavedCard>) : Internal
        data class HandleAddEditCardResult(val result: DataState<String>) : Internal
    }
}
