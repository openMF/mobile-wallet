package org.mifos.mobilewallet.savedcards;

import org.mifos.mobilewallet.base.BasePresenter;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.core.domain.model.Card;

import java.util.List;

/**
 * Created by ankur on 19/May/2018
 */

public interface CardsContract {
    interface CardsView extends BaseView<CardsPresenter> {

        void showSavedCards(List<Card> cards);
    }

    interface CardsPresenter extends BasePresenter {

        void fetchSavedCards();

        void addCard(Card card);

        void editCard(Card card);

        void deleteCard(int position);
    }
}
