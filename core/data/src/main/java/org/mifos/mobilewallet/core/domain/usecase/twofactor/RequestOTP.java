package org.mifos.mobilewallet.core.domain.usecase.twofactor;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 01/June/2018
 */

public class RequestOTP extends UseCase<RequestOTP.RequestValues, RequestOTP.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public RequestOTP(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.requestOTP(requestValues.deliveryMethod)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        getUseCaseCallback().onSuccess(new ResponseValue(s));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String deliveryMethod;

        public RequestValues(String deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final String response;

        public ResponseValue(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }
    }
}
