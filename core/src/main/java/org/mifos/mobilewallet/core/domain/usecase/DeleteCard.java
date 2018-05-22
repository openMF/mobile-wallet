package org.mifos.mobilewallet.core.domain.usecase;

import android.util.Log;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 22/May/2018
 */

public class DeleteCard extends UseCase<DeleteCard.RequestValues, DeleteCard.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public DeleteCard(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.deleteSavedCard(requestValues.cardId)
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
                        Log.d("qxz", "onNext: card deleted.");
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final int cardId;

        public RequestValues(int cardId) {
            this.cardId = cardId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
