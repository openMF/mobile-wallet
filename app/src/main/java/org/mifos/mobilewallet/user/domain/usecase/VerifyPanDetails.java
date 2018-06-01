package org.mifos.mobilewallet.user.domain.usecase;

import org.mifos.mobilewallet.data.rbl.repository.RblRepository;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 22/6/17.
 */

public class VerifyPanDetails extends UseCase<VerifyPanDetails.RequestValues,
        VerifyPanDetails.ResponseValue> {

    private final RblRepository rblRepository;

    @Inject
    public VerifyPanDetails(RblRepository rblRepository) {
        this.rblRepository = rblRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        rblRepository.verifyPanNumber(requestValues.number)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean status) {
                        getUseCaseCallback().onSuccess(new ResponseValue(status));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String number;

        public RequestValues(String number) {
            this.number = number;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final boolean status;

        public ResponseValue(boolean status) {
            this.status = status;
        }

        public boolean isStatus() {
            return status;
        }
    }
}