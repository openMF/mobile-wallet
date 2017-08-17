package mifos.org.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.entity.register.UserVerify;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
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
    protected void executeUseCase(VerifyUser.RequestValues requestValues) {

        apiRepository.verifyUser(requestValues.userVerify)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error verifying user");
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

