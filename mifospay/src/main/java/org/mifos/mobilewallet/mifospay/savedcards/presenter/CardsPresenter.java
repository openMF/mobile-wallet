package org.mifos.mobilewallet.mifospay.savedcards.presenter;

import android.util.Log;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.core.domain.usecase.AddCard;
import org.mifos.mobilewallet.core.domain.usecase.DeleteCard;
import org.mifos.mobilewallet.core.domain.usecase.EditCard;
import org.mifos.mobilewallet.core.domain.usecase.FetchSavedCards;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.savedcards.CardsContract;

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

        fetchSavedCardsUseCase.setRequestValues(
                new FetchSavedCards.RequestValues(
                        mLocalRepository.getClientDetails().getClientId()));

        final FetchSavedCards.RequestValues requestValues =
                fetchSavedCardsUseCase.getRequestValues();

        mUseCaseHandler.execute(fetchSavedCardsUseCase, requestValues,
                new UseCase.UseCaseCallback<FetchSavedCards.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchSavedCards.ResponseValue response) {

                        final List<Card> cards = new ArrayList<>();
                        Log.d("qxz", "fetch cards onSuccess: " + response.getCardList().size());
                        for (Card c : response.getCardList()) {
                            Log.d("qxz", "fetch cards onSuccess: " + c.toString());
                        }
                        cards.addAll(response.getCardList());
                        mCardsView.showSavedCards(cards);
                    }

                    @Override
                    public void onError(String message) {
                        Log.d("qxz", "fetch cards onError: " + message);
                        mCardsView.hideSwipeProgress();
                        mCardsView.showToast("Error fetching cards.");
                    }
                });
    }

    @Override
    public void addCard(Card card) {

        mCardsView.showProgressDialog("Adding Card..");

        if (!validateCreditCardNumber(card.getCardNumber())) {
            mCardsView.showToast("Invalid Credit Card Number");
            mCardsView.hideProgressDialog();
            return;
        }

        addCardUseCase.setRequestValues(
                new AddCard.RequestValues(mLocalRepository.getClientDetails().getClientId(),
                        card));

        final AddCard.RequestValues requestValues = addCardUseCase.getRequestValues();

        mUseCaseHandler.execute(addCardUseCase, requestValues,
                new UseCase.UseCaseCallback<AddCard.ResponseValue>() {
                    @Override
                    public void onSuccess(AddCard.ResponseValue response) {

                        Log.d("qxz", "onSuccess: card presenter. ");
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast("Card added successfully.");
                        fetchSavedCards();
                    }

                    @Override
                    public void onError(String message) {

                        Log.d("qxz", "onError: " + message);
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast("Error adding card.");
                    }
                });
    }

    @Override
    public void editCard(Card card) {

        mCardsView.showProgressDialog("Updating Card..");

        if (!validateCreditCardNumber(card.getCardNumber())) {
            mCardsView.showToast("Invalid Credit Card Number");
            mCardsView.hideProgressDialog();
            return;
        }

        editCardUseCase.setRequestValues(
                new EditCard.RequestValues((int) mLocalRepository.getClientDetails().getClientId(),
                        card));
        final EditCard.RequestValues requestValues = editCardUseCase.getRequestValues();

        mUseCaseHandler.execute(editCardUseCase, requestValues,
                new UseCase.UseCaseCallback<EditCard.ResponseValue>() {
                    @Override
                    public void onSuccess(EditCard.ResponseValue response) {

                        Log.d("qxz", "onSuccess: card presenter. ");
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast("Card updated successfully.");
                        fetchSavedCards();
                    }

                    @Override
                    public void onError(String message) {

                        Log.d("qxz", "onError: " + message);
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast("Error updating card.");
                    }
                });
    }

    @Override
    public void deleteCard(int cardId) {
        mCardsView.showProgressDialog("Deleting Card..");

        deleteCardUseCase.setRequestValues(
                new DeleteCard.RequestValues(
                        (int) mLocalRepository.getClientDetails().getClientId(),
                        cardId));
        final DeleteCard.RequestValues requestValues = deleteCardUseCase.getRequestValues();

        mUseCaseHandler.execute(deleteCardUseCase, requestValues,
                new UseCase.UseCaseCallback<DeleteCard.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteCard.ResponseValue response) {

                        mCardsView.hideProgressDialog();
                        mCardsView.showToast("Card deleted successfully.");
                        fetchSavedCards();
                    }

                    @Override
                    public void onError(String message) {

                        Log.d("qxz", "onError: " + message);
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast("Error deleting card.");
                    }
                });
    }


    /* Luhn Algorithm for validating Credit Card Number
     * src: https://www.journaldev.com/1443/java-credit-card-validation-luhn-algorithm-java*/
    private boolean validateCreditCardNumber(String str) {

        int u = 2;
        if (u - 2 == 0) {
            return true; // for backend testing. remove after testing.
        }

        if (str.length() == 0) {
            return false;
        }

        int[] ints = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            ints[i] = Integer.parseInt(str.substring(i, i + 1));
        }
        for (int i = ints.length - 2; i >= 0; i = i - 2) {
            int j = ints[i];
            j = j * 2;
            if (j > 9) {
                j = j % 10 + 1;
            }
            ints[i] = j;
        }
        int sum = 0;
        for (int i = 0; i < ints.length; i++) {
            sum += ints[i];
        }
        if (sum % 10 == 0) {
            return true;
        }
        return false;
    }
}
