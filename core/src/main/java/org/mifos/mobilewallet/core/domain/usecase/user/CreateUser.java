package org.mifos.mobilewallet.core.domain.usecase.user;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.user.NewUser;
import org.mifos.mobilewallet.core.utils.ErrorJsonMessageHelper;

import javax.inject.Inject;

import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 17/6/17.
 */

public class CreateUser extends UseCase<CreateUser.RequestValues, CreateUser.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    public CreateUser(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        apiRepository.createUser(requestValues.user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CreateUser.ResponseValue>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ErrorJsonMessageHelper.getUserMessage(e);
                        String message = "Error";
                        try {
                            message = ((HttpException) e).response().errorBody().string();
                            message = ErrorJsonMessageHelper.getUserMessage(message);
                        } catch (Exception e1) {
                            message = "Error";
                        }
                        getUseCaseCallback().onError(message);
                    }

                    @Override
                    public void onNext(CreateUser.ResponseValue genericResponse) {
                        getUseCaseCallback().onSuccess(genericResponse);
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final NewUser user;

        public RequestValues(NewUser user) {
            this.user = user;
        }

        public NewUser getUser() {
            return user;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final int resourceId;

        public ResponseValue(int userId) {
            this.resourceId = userId;
        }

        public int getUserId() {
            return resourceId;
        }
    }

}
