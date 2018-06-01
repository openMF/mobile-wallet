package org.mifos.mobilewallet.mifospay.savedcards;


import org.mifos.mobilewallet.core.data.fineract.entity.saved_cards.Card;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * Created by ankur on 19/May/2018
 */

public interface CardsContract {
    interface CardsView extends BaseView<CardsPresenter> {

        void showSavedCards(List<Card> cards);

        void showToast(String message);

        void showProgressDialog(String message);

        void hideProgressDialog();
    }

    interface CardsPresenter extends BasePresenter {

        void fetchSavedCards();

        void addCard(Card card);

        void editCard(Card card);

        void deleteCard(int position);
    }
}
