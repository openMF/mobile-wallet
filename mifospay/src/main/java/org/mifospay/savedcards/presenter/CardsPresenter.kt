package org.mifospay.savedcards.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.savedcards.AddCard
import org.mifospay.core.data.domain.usecase.savedcards.DeleteCard
import org.mifospay.core.data.domain.usecase.savedcards.EditCard
import org.mifospay.core.data.domain.usecase.savedcards.FetchSavedCards
import org.mifospay.R
import org.mifospay.base.BaseView
import org.mifospay.data.local.LocalRepository
import org.mifospay.savedcards.CardsContract
import org.mifospay.savedcards.CardsContract.CardsView
import org.mifospay.common.Constants
import javax.inject.Inject

/**
 * This class is the Presenter component of the Architecture.
 *
 * @author ankur
 * @since 19/May/2018
 */
class CardsPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val fetchSavedCardsUseCase: FetchSavedCards,
    private val addCardUseCase: AddCard,
    private val editCardUseCase: EditCard,
    private val deleteCardUseCase: DeleteCard
) : CardsContract.CardsPresenter {

    private var mCardsView: CardsView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mCardsView = baseView as CardsView?
        mCardsView!!.setPresenter(this)
    }

    /**
     * An overridden method from Contract to fetch Saved Cards.
     */
    override fun fetchSavedCards() {
        mCardsView!!.showFetchingProcess()
        fetchSavedCardsUseCase!!.walletRequestValues = FetchSavedCards.RequestValues(
            mLocalRepository.clientDetails.clientId
        )
        val requestValues = fetchSavedCardsUseCase!!.walletRequestValues
        mUseCaseHandler.execute(fetchSavedCardsUseCase, requestValues,
            object : UseCaseCallback<FetchSavedCards.ResponseValue> {
                override fun onSuccess(response: FetchSavedCards.ResponseValue) {
                    response?.cardList?.let { mCardsView!!.showSavedCards(it as List<Card>) }
                }

                override fun onError(message: String) {
                    mCardsView!!.hideSwipeProgress()
                    mCardsView!!.showErrorStateView(
                        R.drawable.ic_error_state,
                        R.string.error_oops,
                        R.string.error_no_cards_found
                    )
                }
            })
    }

    /**
     * An overridden method from Contract to Add a new Card.
     *
     * @param card : The card to be added.
     */
    override fun addCard(card: Card?) {
        mCardsView!!.showProgressDialog(Constants.ADDING_CARD)
        if (!validateCreditCardNumber(card!!.cardNumber)) {
            mCardsView!!.showToast(Constants.INVALID_CREDIT_CARD_NUMBER)
            mCardsView!!.hideProgressDialog()
            return
        }
        addCardUseCase!!.walletRequestValues = AddCard.RequestValues(
            mLocalRepository.clientDetails.clientId,
            card
        )
        val requestValues = addCardUseCase!!.walletRequestValues
        mUseCaseHandler.execute(addCardUseCase, requestValues,
            object : UseCaseCallback<AddCard.ResponseValue> {
                override fun onSuccess(response: AddCard.ResponseValue) {
                    mCardsView!!.hideProgressDialog()
                    mCardsView!!.showToast(Constants.CARD_ADDED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    mCardsView!!.hideSwipeProgress()
                    mCardsView!!.showToast(Constants.ERROR_ADDING_CARD)
                }
            })
    }

    /**
     * An overridden method from Contract to edit a Card.
     *
     * @param card : Card to be edited.
     */
    override fun editCard(card: Card?) {
        mCardsView!!.showProgressDialog(Constants.UPDATING_CARD)
        if (!validateCreditCardNumber(card!!.cardNumber)) {
            mCardsView!!.showToast(Constants.INVALID_CREDIT_CARD_NUMBER)
            mCardsView!!.hideProgressDialog()
            return
        }
        editCardUseCase!!.walletRequestValues = EditCard.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            card
        )
        val requestValues = editCardUseCase!!.walletRequestValues
        mUseCaseHandler.execute(editCardUseCase, requestValues,
            object : UseCaseCallback<EditCard.ResponseValue> {
                override fun onSuccess(response: EditCard.ResponseValue) {
                    mCardsView!!.hideProgressDialog()
                    mCardsView!!.showToast(Constants.CARD_UPDATED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    mCardsView!!.hideProgressDialog()
                    mCardsView!!.showToast(Constants.ERROR_UPDATING_CARD)
                }
            })
    }

    /**
     * An overridden method from Contract to delete a particular card.
     *
     * @param cardId : Card to be deleted.
     */
    override fun deleteCard(cardId: Int) {
        mCardsView!!.showProgressDialog(Constants.DELETING_CARD)
        deleteCardUseCase!!.walletRequestValues = DeleteCard.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            cardId
        )
        val requestValues = deleteCardUseCase!!.walletRequestValues
        mUseCaseHandler.execute(deleteCardUseCase, requestValues,
            object : UseCaseCallback<DeleteCard.ResponseValue> {
                override fun onSuccess(response: DeleteCard.ResponseValue) {
                    mCardsView!!.hideProgressDialog()
                    mCardsView!!.showToast(Constants.CARD_DELETED_SUCCESSFULLY)
                    fetchSavedCards()
                }

                override fun onError(message: String) {
                    mCardsView!!.hideProgressDialog()
                    mCardsView!!.showToast(Constants.ERROR_DELETING_CARD)
                }
            })
    }

    /**
     * An utility function to validate a Credit Card Nnumber.
     *
     * @param str : String to be validated
     * Luhn Algorithm for validating Credit Card Number
     * src: https://www.journaldev
     * .com/1443/kotlin-credit-card-validation-luhn-algorithm-kotlin
     */
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