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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifospay.core.model.entity.savedcards.Card
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.savedcards.AddCard
import org.mifospay.core.data.domain.usecase.savedcards.DeleteCard
import org.mifospay.core.data.domain.usecase.savedcards.EditCard
import org.mifospay.core.data.domain.usecase.savedcards.FetchSavedCards
import org.mifospay.core.data.repository.local.LocalRepository

class CardsScreenViewModel (
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val addCardUseCase: AddCard,
    private val fetchSavedCardsUseCase: FetchSavedCards,
    private val editCardUseCase: EditCard,
    private val deleteCardUseCase: DeleteCard,
) : ViewModel() {

    private val mSearchQuery = MutableStateFlow("")
    private val searchQuery: StateFlow<String> = mSearchQuery.asStateFlow()

    private val mCardState = MutableStateFlow<CardsUiState>(CardsUiState.Loading)
    val cardState: StateFlow<CardsUiState> = mCardState.asStateFlow()

    init {
        fetchSavedCards()
    }

    val cardListUiState: StateFlow<CardsUiState> = searchQuery
        .map { q ->
            when (mCardState.value) {
                is CardsUiState.CreditCardForm -> {
                    val cardList = (cardState.value as CardsUiState.CreditCardForm).cards
                    val filterCards = cardList.filter {
                        it.cardNumber.lowercase().contains(q.lowercase())
                        it.firstName.lowercase().contains(q.lowercase())
                        it.lastName.lowercase().contains(q.lowercase())
                        it.cvv.lowercase().contains(q.lowercase())
                        it.expiryDate.lowercase().contains(q.lowercase())
                    }
                    CardsUiState.CreditCardForm(filterCards)
                }

                else -> CardsUiState.CreditCardForm(arrayListOf())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CardsUiState.CreditCardForm(arrayListOf()),
        )

    fun updateSearchQuery(query: String) {
        mSearchQuery.update { query }
    }

    fun fetchSavedCards() {
        fetchSavedCardsUseCase.walletRequestValues = FetchSavedCards.RequestValues(
            mLocalRepository.clientDetails.clientId,
        )
        val requestValues = fetchSavedCardsUseCase.walletRequestValues
        mUseCaseHandler.execute(
            fetchSavedCardsUseCase,
            requestValues,
            object : UseCase.UseCaseCallback<FetchSavedCards.ResponseValue> {
                override fun onSuccess(response: FetchSavedCards.ResponseValue) {
                    response.cardList.let { mCardState.value = CardsUiState.CreditCardForm(it) }
                }

                override fun onError(message: String) {
                    mCardState.value = CardsUiState.Error
                }
            },
        )
    }

    fun addCard(card: Card) {
        addCardUseCase.walletRequestValues = AddCard.RequestValues(
            mLocalRepository.clientDetails.clientId,
            card,
        )
        val requestValues = addCardUseCase.walletRequestValues
        mUseCaseHandler.execute(
            addCardUseCase,
            requestValues,
            object : UseCase.UseCaseCallback<AddCard.ResponseValue> {
                override fun onSuccess(response: AddCard.ResponseValue) {
                    mCardState.value = CardsUiState.Success(CardsUiEvent.CARD_ADDED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    mCardState.value = CardsUiState.Error
                }
            },
        )
    }

    fun editCard(card: Card) {
        editCardUseCase.walletRequestValues = EditCard.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            card,
        )
        val requestValues = editCardUseCase.walletRequestValues
        mUseCaseHandler.execute(
            editCardUseCase,
            requestValues,
            object : UseCase.UseCaseCallback<EditCard.ResponseValue> {
                override fun onSuccess(response: EditCard.ResponseValue) {
                    mCardState.value = CardsUiState.Success(CardsUiEvent.CARD_UPDATED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    mCardState.value = CardsUiState.Error
                }
            },
        )
    }

    fun deleteCard(cardId: Int) {
        deleteCardUseCase.walletRequestValues = DeleteCard.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            cardId,
        )
        val requestValues = deleteCardUseCase.walletRequestValues
        mUseCaseHandler.execute(
            deleteCardUseCase,
            requestValues,
            object : UseCase.UseCaseCallback<DeleteCard.ResponseValue> {
                override fun onSuccess(response: DeleteCard.ResponseValue) {
                    mCardState.value = CardsUiState.Success(CardsUiEvent.CARD_DELETED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    mCardState.value = CardsUiState.Error
                }
            },
        )
    }
}

sealed interface CardsUiState {
    data class CreditCardForm(
        val cards: List<Card>,
    ) : CardsUiState

    data object Empty : CardsUiState
    data object Error : CardsUiState
    data object Loading : CardsUiState
    data class Success(val cardsUiEvent: CardsUiEvent) : CardsUiState
}

enum class CardsUiEvent {
    CARD_ADDED_SUCCESSFULLY,
    CARD_UPDATED_SUCCESSFULLY,
    CARD_DELETED_SUCCESSFULLY,
}
