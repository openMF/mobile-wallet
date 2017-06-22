package org.mifos.mobilewallet.auth.domain.usecase;

import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.repository.ApiRepository;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 16/6/17.
 */

public class AuthenticateUser extends UseCase<AuthenticateUser.RequestValues,
        AuthenticateUser.ResponseValue> {

    private final ApiRepository apiRepository;

    @Inject
    public AuthenticateUser(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {

        apiRepository.login(requestValues.username,
                requestValues.password).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error logging in");
                    }

                    @Override
                    public void onNext(User user) {
                        getUseCaseCallback().onSuccess(new ResponseValue(user));
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
