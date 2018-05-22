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
 * Created by ankur on 22/May/2018
 */

public class EditCard extends UseCase<EditCard.RequestValues, EditCard.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public EditCard(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.editSavedCard(requestValues.cardId, requestValues.card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        Log.d("qxz", "onNext: card updated");
                    }
                });

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final int cardId;
        private final Card card;

        public RequestValues(int cardId, Card card) {
            this.cardId = cardId;
            this.card = card;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
