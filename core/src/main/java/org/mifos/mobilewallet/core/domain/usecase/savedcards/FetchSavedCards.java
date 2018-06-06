package org.mifos.mobilewallet.core.domain.usecase.savedcards;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 21/May/2018
 */

public class FetchSavedCards extends UseCase<FetchSavedCards.RequestValues,
        FetchSavedCards.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchSavedCards(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.fetchSavedCards(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Card>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(List<Card> cards) {
                        if (cards != null) {
                            getUseCaseCallback().onSuccess(new
                                    ResponseValue(cards));
                        } else {
                            getUseCaseCallback().onError(Constants.NO_SAVED_CARDS);
                        }
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final long clientId;

        public RequestValues(long clientId) {
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Card> cardList;

        public ResponseValue(List<Card> cards) {
            this.cardList = cards;
        }

        public List<Card> getCardList() {
            return cardList;
        }
    }
}
