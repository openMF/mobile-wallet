package org.mifos.mobilewallet.core.domain.usecase.user;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.UserEntity;
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.UserEntityMapper;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.domain.model.user.User;
import org.mifos.mobilewallet.core.utils.Constants;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 16/6/17.
 */

public class AuthenticateUser extends UseCase<AuthenticateUser.RequestValues,
        AuthenticateUser.ResponseValue> {

    private final FineractRepository apiRepository;

    @Inject
    UserEntityMapper userEntityMapper;

    @Inject
    public AuthenticateUser(FineractRepository apiRepository) {
        this.apiRepository = apiRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        apiRepository.loginSelf(requestValues.username,
                requestValues.password).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UserEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(Constants.ERROR_LOGGING_IN);
                    }

                    @Override
                    public void onNext(final UserEntity user) {
                        getUseCaseCallback().onSuccess(new
                                ResponseValue(userEntityMapper.transform(user)));
                    }
                });

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String username, password;

        public RequestValues(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final User user;

        public ResponseValue(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }
}
