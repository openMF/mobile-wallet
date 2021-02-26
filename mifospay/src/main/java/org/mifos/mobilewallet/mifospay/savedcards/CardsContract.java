package org.mifos.mobilewallet.mifospay.savedcards;


import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * This a contract class working as an Interface for UI
 * and Presenter components of the SavedCards Architecture.
 *
 * @author ankur
 * @since 19/May/2018
 */

public interface CardsContract {

    /**
     * Defines all the functions in UI Component.
     */
    interface CardsView extends BaseView<CardsPresenter> {

        void showSavedCards(List<Card> cards);

        void showToast(String message);

        void showProgressDialog(String message);

        void hideProgressDialog();

        void showErrorStateView(int drawable, int title, int subtitle);

        void showFetchingProcess();

        void hideSwipeProgress();
    }

    /**
     * Defines all the functions in Presenter Component.
     */
    interface CardsPresenter extends BasePresenter {

        void fetchSavedCards();

        void addCard(Card card);

        void editCard(Card card);

        void deleteCard(int position);
    }
}
