package org.mifos.mobilewallet.core.domain.usecase;

import android.util.Log;

import com.google.gson.JsonArray;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.Card;

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
    protected void executeUseCase(FetchSavedCards.RequestValues requestValues) {
        mFineractRepository.fetchSavedCards(requestValues.userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("qxz", "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(JsonArray jsonElements) {
                        Log.d("qxz", "onNext: " + jsonElements);
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final long userId;

        public RequestValues(long userId) {
            this.userId = userId;
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
