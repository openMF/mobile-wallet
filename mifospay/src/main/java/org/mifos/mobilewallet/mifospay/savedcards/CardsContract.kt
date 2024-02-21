package org.mifos.mobilewallet.mifospay.savedcards

import com.mifos.mobilewallet.model.entity.savedcards.Card
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * This a contract class working as an Interface for UI
 * and Presenter components of the SavedCards Architecture.
 *
 * @author ankur
 * @since 19/May/2018
 */
interface CardsContract {
    /**
     * Defines all the functions in UI Component.
     */
    interface CardsView : BaseView<CardsPresenter?> {
        fun showSavedCards(cards: List<Card>)
        fun showToast(message: String?)
        fun showProgressDialog(message: String?)
        fun hideProgressDialog()
        fun showErrorStateView(drawable: Int, title: Int, subtitle: Int)
        fun showFetchingProcess()
        fun hideSwipeProgress()
    }

    /**
     * Defines all the functions in Presenter Component.
     */
    interface CardsPresenter : BasePresenter {
        fun fetchSavedCards()
        fun addCard(card: Card?)
        fun editCard(card: Card?)
        fun deleteCard(position: Int)
    }
}