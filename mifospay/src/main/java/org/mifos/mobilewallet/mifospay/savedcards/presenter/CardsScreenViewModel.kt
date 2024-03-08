package org.mifos.mobilewallet.mifospay.savedcards.presenter

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.mobilewallet.model.entity.savedcards.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.savedcards.AddCard
import org.mifos.mobilewallet.core.domain.usecase.savedcards.DeleteCard
import org.mifos.mobilewallet.core.domain.usecase.savedcards.EditCard
import org.mifos.mobilewallet.core.domain.usecase.savedcards.FetchSavedCards
import org.mifos.mobilewallet.mifospay.MifosPayApp
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.savedcards.ui.sampleCardies
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

@HiltViewModel
class CardsScreenViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val addCardUseCase : AddCard,
    private val fetchSavedCardsUseCase : FetchSavedCards,
    private val editCardUseCase: EditCard,
    private val deleteCardUseCase: DeleteCard
): ViewModel() {

    private val _cardState = MutableStateFlow<CardsUiState>(CardsUiState.WorkingUiState(sampleCardies))
    val cardState: StateFlow<CardsUiState> = _cardState.asStateFlow()

    init {
        fetchSavedCards()
    }

    private fun fetchSavedCards() {
        viewModelScope.launch {
            fetchSavedCardsUseCase.requestValues = FetchSavedCards.RequestValues(
                mLocalRepository.clientDetails.clientId
            )
            val requestValues = fetchSavedCardsUseCase.requestValues
            mUseCaseHandler.execute(fetchSavedCardsUseCase, requestValues,
                object : UseCase.UseCaseCallback<FetchSavedCards.ResponseValue?> {
                    override fun onSuccess(response: FetchSavedCards.ResponseValue?) {
                       response?.cardList?.let {  _cardState.value = CardsUiState.WorkingUiState(it) }
                    }
                    override fun onError(message: String) {
                        _cardState.value = CardsUiState.ErrorUiState
                    }
                })
        }
    }

    //Todo: addCard will be used by AddCardDialog after migration
    fun addCard(card: Card?) {
        if (!validateCreditCardNumber(card!!.cardNumber)) {
            Toast.makeText(MifosPayApp.context, Constants.INVALID_CREDIT_CARD_NUMBER, Toast.LENGTH_SHORT).show()
            return
        }
        addCardUseCase.requestValues = AddCard.RequestValues(
            mLocalRepository.clientDetails.clientId,
            card
        )
        val requestValues = addCardUseCase.requestValues
        mUseCaseHandler.execute(addCardUseCase, requestValues,
            object : UseCase.UseCaseCallback<AddCard.ResponseValue?> {
                override fun onSuccess(response: AddCard.ResponseValue?) {
                    //mCardsView!!.hideProgressDialog()
                    Toast.makeText(MifosPayApp.context, Constants.CARD_ADDED_SUCCESSFULLY, Toast.LENGTH_SHORT).show()
                    //mCardsView!!.showToast(Constants.CARD_ADDED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                  //  mCardsView!!.hideSwipeProgress()
                    Toast.makeText(MifosPayApp.context, Constants.ERROR_ADDING_CARD, Toast.LENGTH_SHORT).show()
                  //  mCardsView!!.showToast(Constants.ERROR_ADDING_CARD)
                }
            })
    }

    //Todo: editCard will be used by AddCardDialog after migration
    fun editCard(card: Card?) {
        if (card != null) {
            if (!validateCreditCardNumber(card.cardNumber)) {
                Toast.makeText(MifosPayApp.context, Constants.INVALID_CREDIT_CARD_NUMBER, Toast.LENGTH_SHORT).show()
                return
            }
        }
        editCardUseCase.requestValues = card?.let {
            EditCard.RequestValues(
                mLocalRepository.clientDetails.clientId.toInt(),
                it
            )
        }
        val requestValues = editCardUseCase.requestValues
        mUseCaseHandler.execute(editCardUseCase, requestValues,
            object : UseCase.UseCaseCallback<EditCard.ResponseValue?> {
                override fun onSuccess(response: EditCard.ResponseValue?) {
                    Toast.makeText(MifosPayApp.context, Constants.CARD_UPDATED_SUCCESSFULLY, Toast.LENGTH_SHORT).show()
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    Toast.makeText(MifosPayApp.context, Constants.ERROR_UPDATING_CARD, Toast.LENGTH_SHORT).show()
                }
            })
    }

    //Todo: deleteCard will be used by AddCardDialog after migration
    fun deleteCard(cardId: Int) {

        deleteCardUseCase.requestValues = DeleteCard.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            cardId
        )
        val requestValues = deleteCardUseCase.requestValues
        mUseCaseHandler.execute(deleteCardUseCase, requestValues,
            object : UseCase.UseCaseCallback<DeleteCard.ResponseValue?> {
                override fun onSuccess(response: DeleteCard.ResponseValue?) {
                    Toast.makeText(MifosPayApp.context, Constants.CARD_DELETED_SUCCESSFULLY, Toast.LENGTH_SHORT).show()
                    fetchSavedCards()
                }
                override fun onError(message: String) {
                    Toast.makeText(MifosPayApp.context, Constants.ERROR_DELETING_CARD, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun validateCreditCardNumber(str: String): Boolean {
        val u = 2
        if (u - 2 == 0) {
            return true // for backend testing. remove after testing.
        }
        if (str.length == 0) {
            return false
        }
        val ints = IntArray(str.length)
        for (i in 0 until str.length) {
            ints[i] = str.substring(i, i + 1).toInt()
        }
        run {
            var i = ints.size - 2
            while (i >= 0) {
                var j = ints[i]
                j = j * 2
                if (j > 9) {
                    j = j % 10 + 1
                }
                ints[i] = j
                i = i - 2
            }
        }
        var sum = 0
        for (i in ints.indices) {
            sum += ints[i]
        }
        return sum % 10 == 0
    }

}



sealed interface CardsUiState {
    data class WorkingUiState(
        val cards: List<Card?>
    ) : CardsUiState
    data object EmptyUiState : CardsUiState
    data object ErrorUiState : CardsUiState
    data object LoadingState : CardsUiState
}