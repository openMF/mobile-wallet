package org.mifos.mobilewallet.core.domain.usecase.user;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.register.UserVerify;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 17/8/17.
 */

public class VerifyUser extends UseCase<VerifyUser.RequestValues,
        VerifyUser.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    public VerifyUser(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        apiRepository.verifyUser(requestValues.userVerify)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_VERIFYING_USER);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getUseCaseCallback().onSuccess(new ResponseValue());
                    }
                });

    }


    public static final class RequestValues implements UseCase.RequestValues {

        private final UserVerify userVerify;

        public RequestValues(UserVerify userVerify) {
            this.userVerify = userVerify;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        public ResponseValue() {

        }

    }
}

