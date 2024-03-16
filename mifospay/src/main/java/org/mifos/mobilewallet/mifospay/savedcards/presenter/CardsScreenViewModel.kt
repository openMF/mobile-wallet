package org.mifos.mobilewallet.mifospay.savedcards.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.mobilewallet.model.entity.savedcards.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.savedcards.AddCard
import org.mifos.mobilewallet.core.domain.usecase.savedcards.DeleteCard
import org.mifos.mobilewallet.core.domain.usecase.savedcards.EditCard
import org.mifos.mobilewallet.core.domain.usecase.savedcards.FetchSavedCards
import org.mifos.mobilewallet.mifospay.common.CreditCardUtils.validateCreditCardNumber
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

@HiltViewModel
class CardsScreenViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val addCardUseCase: AddCard,
    private val fetchSavedCardsUseCase: FetchSavedCards,
    private val editCardUseCase: EditCard,
    private val deleteCardUseCase: DeleteCard
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cardState = MutableStateFlow<CardsUiState>(CardsUiState.Loading)
    val cardState: StateFlow<CardsUiState> = _cardState.asStateFlow()

    init {
        fetchSavedCards()
    }

    val cardListUiState: StateFlow<CardsUiState> = searchQuery
        .map { q ->
            when (_cardState.value) {
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
            initialValue = CardsUiState.CreditCardForm(arrayListOf())
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun fetchSavedCards() {
        fetchSavedCardsUseCase.walletRequestValues = FetchSavedCards.RequestValues(
            mLocalRepository.clientDetails.clientId
        )
        val requestValues = fetchSavedCardsUseCase.walletRequestValues
        mUseCaseHandler.execute(fetchSavedCardsUseCase, requestValues,
            object : UseCase.UseCaseCallback<FetchSavedCards.ResponseValue> {
                override fun onSuccess(response: FetchSavedCards.ResponseValue) {
                    response.cardList.let { _cardState.value = CardsUiState.CreditCardForm(it) }
                }

                override fun onError(message: String) {
                    _cardState.value = CardsUiState.Error
                }
            })
    }

    fun addCard(
        card: Card?,
        onValidationSuccess: (String) -> Unit,
        onValidationFail: (String) -> Unit
    ) {
        if (!validateCreditCardNumber(card!!.cardNumber)) {
            onValidationFail(Constants.ERROR_ADDING_CARD)
            return
        }
        addCardUseCase.walletRequestValues = AddCard.RequestValues(
            mLocalRepository.clientDetails.clientId,
            card
        )
        val requestValues = addCardUseCase.walletRequestValues
        mUseCaseHandler.execute(addCardUseCase, requestValues,
            object : UseCase.UseCaseCallback<AddCard.ResponseValue> {
                override fun onSuccess(response: AddCard.ResponseValue) {
                    onValidationSuccess(Constants.CARD_ADDED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    onValidationFail(Constants.ERROR_ADDING_CARD)
                }
            })
    }

    fun editCard(
        card: Card?,
        onValidationSuccess: (String) -> Unit,
        onValidationFail: (String) -> Unit
    ) {
        if (!validateCreditCardNumber(card!!.cardNumber)) {
            onValidationFail(Constants.INVALID_CREDIT_CARD_NUMBER)
            return
        }
        editCardUseCase.walletRequestValues = EditCard.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            card
        )
        val requestValues = editCardUseCase.walletRequestValues
        mUseCaseHandler.execute(editCardUseCase, requestValues,
            object : UseCase.UseCaseCallback<EditCard.ResponseValue> {
                override fun onSuccess(response: EditCard.ResponseValue) {
                    onValidationSuccess(Constants.CARD_UPDATED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    onValidationFail(Constants.ERROR_UPDATING_CARD)
                }
            })
    }

    fun deleteCard(
        cardId: Int,
        onValidationSuccess: (String) -> Unit,
        onValidationFail: (String) -> Unit
    ) {
        deleteCardUseCase.walletRequestValues = DeleteCard.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            cardId
        )
        val requestValues = deleteCardUseCase.walletRequestValues
        mUseCaseHandler.execute(deleteCardUseCase, requestValues,
            object : UseCase.UseCaseCallback<DeleteCard.ResponseValue> {
                override fun onSuccess(response: DeleteCard.ResponseValue) {
                    onValidationSuccess(Constants.CARD_DELETED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    onValidationFail(Constants.ERROR_DELETING_CARD)
                }
            })
    }
}


sealed interface CardsUiState {
    data class CreditCardForm(
        val cards: List<Card>
    ) : CardsUiState

    data object Empty : CardsUiState
    data object Error : CardsUiState
    data object Loading : CardsUiState
}