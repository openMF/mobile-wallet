package org.mifos.mobilewallet.savedcards.presenter;

import android.util.Log;

import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Card;
import org.mifos.mobilewallet.core.domain.usecase.AddCard;
import org.mifos.mobilewallet.core.domain.usecase.DeleteCard;
import org.mifos.mobilewallet.core.domain.usecase.EditCard;
import org.mifos.mobilewallet.core.domain.usecase.FetchSavedCards;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.savedcards.CardsContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ankur on 19/May/2018
 */

public class CardsPresenter implements CardsContract.CardsPresenter {
    private final LocalRepository mLocalRepository;

    private final UseCaseHandler mUseCaseHandler;

    @Inject
    AddCard addCardUseCase;

    @Inject
    FetchSavedCards fetchSavedCardsUseCase;

    @Inject
    EditCard editCardUseCase;

    @Inject
    DeleteCard deleteCardUseCase;

    private CardsContract.CardsView mCardsView;

    @Inject
    public CardsPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        mUseCaseHandler = useCaseHandler;
        mLocalRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mCardsView = (CardsContract.CardsView) baseView;
        mCardsView.setPresenter(this);
    }


    @Override
    public void fetchSavedCards() {
        List<Card> cards = new ArrayList<>();
//        cards.add(new Card("6433131", "545", "11"));

        fetchSavedCardsUseCase.setRequestValues(
                new FetchSavedCards.RequestValues(
                        mLocalRepository.getClientDetails().getClientId()));

        final FetchSavedCards.RequestValues requestValues =
                fetchSavedCardsUseCase.getRequestValues();

        mUseCaseHandler.execute(fetchSavedCardsUseCase, requestValues,
                new UseCase.UseCaseCallback<FetchSavedCards.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchSavedCards.ResponseValue response) {
                        Log.d("qxz", "fetch cards onSuccess: " + response);
                    }

                    @Override
                    public void onError(String message) {
                        Log.d("qxz", "fetch cards onError: " + message);
                    }
                });

        mCardsView.showSavedCards(cards);
    }

    @Override
    public void addCard(Card card) {
        addCardUseCase.setRequestValues(
                new AddCard.RequestValues(mLocalRepository.getClientDetails().getClientId(),
                        card));

        final AddCard.RequestValues requestValues = addCardUseCase.getRequestValues();

        mUseCaseHandler.execute(addCardUseCase, requestValues,
                new UseCase.UseCaseCallback<AddCard.ResponseValue>() {
                    @Override
                    public void onSuccess(AddCard.ResponseValue response) {
                        Log.d("qxz", "onSuccess: card presenter.");
                    }

                    @Override
                    public void onError(String message) {
                        Log.d("qxz", "onError: " + message);
                    }
                });
    }

    @Override
    public void editCard(Card card) {
        editCardUseCase.setRequestValues(new EditCard.RequestValues(1, card));
        final EditCard.RequestValues requestValues = editCardUseCase.getRequestValues();

        mUseCaseHandler.execute(editCardUseCase, requestValues,
                new UseCase.UseCaseCallback<EditCard.ResponseValue>() {
                    @Override
                    public void onSuccess(EditCard.ResponseValue response) {
                        // update cardslist
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    @Override
    public void deleteCard(int position) {
        deleteCardUseCase.setRequestValues(new DeleteCard.RequestValues(1));
        final DeleteCard.RequestValues requestValues = deleteCardUseCase.getRequestValues();

        mUseCaseHandler.execute(deleteCardUseCase, requestValues,
                new UseCase.UseCaseCallback<DeleteCard.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteCard.ResponseValue response) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
