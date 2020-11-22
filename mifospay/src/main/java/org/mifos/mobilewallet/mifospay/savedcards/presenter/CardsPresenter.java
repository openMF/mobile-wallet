package org.mifos.mobilewallet.mifospay.savedcards.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.core.domain.usecase.savedcards.AddCard;
import org.mifos.mobilewallet.core.domain.usecase.savedcards.DeleteCard;
import org.mifos.mobilewallet.core.domain.usecase.savedcards.EditCard;
import org.mifos.mobilewallet.core.domain.usecase.savedcards.FetchSavedCards;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.savedcards.CardsContract;

import javax.inject.Inject;

/**
 * This class is the Presenter component of the Architecture.
 *
 * @author ankur
 * @since 19/May/2018
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

    /**
     * An overridden method from Contract to fetch Saved Cards.
     */
    @Override
    public void fetchSavedCards() {
        mCardsView.showFetchingProcess();
        fetchSavedCardsUseCase.setRequestValues(
                new FetchSavedCards.RequestValues(
                        mLocalRepository.getClientDetails().getClientId()));

        final FetchSavedCards.RequestValues requestValues =
                fetchSavedCardsUseCase.getRequestValues();

        mUseCaseHandler.execute(fetchSavedCardsUseCase, requestValues,
                new UseCase.UseCaseCallback<FetchSavedCards.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchSavedCards.ResponseValue response) {
                        mCardsView.showSavedCards(response.getCardList());
                    }

                    @Override
                    public void onError(String message) {
                        mCardsView.hideSwipeProgress();
                        mCardsView.showErrorStateView(R.drawable.ic_error_state,
                                R.string.error_oops,
                                R.string.error_no_cards_found);
                    }
                });
    }

    /**
     * An overridden method from Contract to Add a new Card.
     *
     * @param card : The card to be added.
     */
    @Override
    public void addCard(Card card) {

        mCardsView.showProgressDialog(mCardsView.getContext().getString(R.string.adding_card));

        if (!validateCreditCardNumber(card.getCardNumber())) {
            mCardsView.showToast(mCardsView.getContext()
                    .getString(R.string.invalid_credit_card_number));
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
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast(mCardsView
                                .getContext()
                                .getString(R.string.card_added_successfully));
                        fetchSavedCards();
                    }

                    @Override
                    public void onError(String message) {
                        mCardsView.hideSwipeProgress();
                        mCardsView.showToast(mCardsView
                                .getContext()
                                .getString(R.string.error_adding_card));
                    }
                });
    }

    /**
     * An overridden method from Contract to edit a Card.
     *
     * @param card : Card to be edited.
     */
    @Override
    public void editCard(Card card) {

        mCardsView.showProgressDialog(mCardsView
                .getContext()
                .getString(R.string.updating_card));

        if (!validateCreditCardNumber(card.getCardNumber())) {
            mCardsView.showToast(mCardsView
                    .getContext()
                    .getString(R.string.invalid_credit_card_number));
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
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast(mCardsView
                                .getContext()
                                .getString(R.string.card_updated_successfully));
                        fetchSavedCards();
                    }

                    @Override
                    public void onError(String message) {
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast(mCardsView
                                .getContext()
                                .getString(R.string.error_updating_card));
                    }
                });
    }

    /**
     * An overridden method from Contract to delete a particular card.
     *
     * @param cardId : Card to be deleted.
     */
    @Override
    public void deleteCard(int cardId) {
        mCardsView.showProgressDialog(mCardsView
                .getContext()
                .getString(R.string.deleting_card));

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
                        mCardsView.showToast(mCardsView
                                .getContext()
                                .getString(R.string.card_deleted_successfully));
                        fetchSavedCards();
                    }

                    @Override
                    public void onError(String message) {
                        mCardsView.hideProgressDialog();
                        mCardsView.showToast(mCardsView
                                .getContext()
                                .getString(R.string.error_deleting_card));
                    }
                });
    }


    /**
     * An utility function to validate a Credit Card Nnumber.
     *
     * @param str : String to be validated
     *            Luhn Algorithm for validating Credit Card Number
     *            src: https://www.journaldev
     *            .com/1443/java-credit-card-validation-luhn-algorithm-java
     */
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
        return sum % 10 == 0;
    }
}
