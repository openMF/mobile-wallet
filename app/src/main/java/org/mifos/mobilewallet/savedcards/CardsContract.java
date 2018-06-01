package org.mifos.mobilewallet.savedcards;

import org.mifos.mobilewallet.base.BasePresenter;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;

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
