package org.mifos.mobilewallet.core.domain.usecase.twofactor;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.twofactor.AccessToken;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 01/June/2018
 */

public class ValidateOTP extends UseCase<ValidateOTP.RequestValues, ValidateOTP.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public ValidateOTP(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFineractRepository.validateToken(requestValues.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AccessToken>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        getUseCaseCallback().onSuccess(new ResponseValue(accessToken));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String token;

        public RequestValues(String token) {
            this.token = token;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final AccessToken accessToken;

        public ResponseValue(AccessToken accessToken) {
            this.accessToken = accessToken;
        }

        public AccessToken getAccessToken() {
            return accessToken;
        }
    }
}
