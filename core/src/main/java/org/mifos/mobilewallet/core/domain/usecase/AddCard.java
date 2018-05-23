package org.mifos.mobilewallet.core.domain.usecase;

import android.util.Log;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.Card;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 21/May/2018
 */

public class AddCard extends UseCase<AddCard.RequestValues, AddCard.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public AddCard(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.addSavedCards(requestValues.clientId, requestValues.card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        Log.d("qxz", "onNext: card added " + genericResponse.toString());
                        getUseCaseCallback().onSuccess(new AddCard.ResponseValue());
                    }
                });
    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final long clientId;
        private final Card card;

        public RequestValues(long clientId, Card card) {
            this.clientId = clientId;
            this.card = card;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
