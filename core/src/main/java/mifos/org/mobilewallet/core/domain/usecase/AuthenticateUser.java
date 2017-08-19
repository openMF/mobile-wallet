package mifos.org.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.domain.model.User;
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
    public AuthenticateUser(FineractRepository apiRepository) {
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
                    public void onNext(final User user) {
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
