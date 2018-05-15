package org.mifos.mobilewallet.auth.domain.usecase;

import org.mifos.mobilewallet.data.rbl.repository.RblRepository;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 16/7/17.
 */

public class VerifyAadharDetails extends UseCase<VerifyAadharDetails.RequestValues,
        VerifyAadharDetails.ResponseValue> {

    private final RblRepository rblRepository;

    @Inject
    public VerifyAadharDetails(RblRepository rblRepository) {
        this.rblRepository = rblRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        switch (requestValues.requestCode) {
            case 0:
                rblRepository.verifyAadharNumber(requestValues.number)
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
                                getUseCaseCallback().onSuccess(new
                                        ResponseValue(status));
                            }
                        });
                break;
            case 1:
                rblRepository.generateAadharOtp()
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
                                getUseCaseCallback().onSuccess(new
                                        ResponseValue(status));
                            }
                        });
                break;
            case 2:
                rblRepository.verifyAadharOtp(requestValues.number)
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
                                getUseCaseCallback().onSuccess(new
                                        ResponseValue(status));
                            }
                        });
                break;
        }

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String number;

        //0-Validate Aadhar number;
        //1-Generate AAdhar OTP
        //2-Verify Aadhar OTP
        private final int requestCode;

        public RequestValues(String number, int requestCode) {
            this.number = number;
            this.requestCode = requestCode;
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
