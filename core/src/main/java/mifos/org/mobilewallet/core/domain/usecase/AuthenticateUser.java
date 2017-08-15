package mifos.org.mobilewallet.core.domain.usecase;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;
import mifos.org.mobilewallet.core.data.fineract.repository.FineractRepository;
import mifos.org.mobilewallet.core.data.local.PreferencesHelper;
import mifos.org.mobilewallet.core.domain.model.User;
import mifos.org.mobilewallet.core.utils.Constants;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 16/6/17.
 */

public class AuthenticateUser extends UseCase<AuthenticateUser.RequestValues,
        AuthenticateUser.ResponseValue> {

    private final FineractRepository apiRepository;
    private PreferencesHelper preferencesHelper;

    @Inject
    public AuthenticateUser(FineractRepository apiRepository, PreferencesHelper preferencesHelper) {
        this.apiRepository = apiRepository;
        this.preferencesHelper = preferencesHelper;
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
                        saveUserDetails(user);
                        getUseCaseCallback().onSuccess(new ResponseValue(user));
                    }
                });

    }

    private void saveUserDetails(User user) {
        final String userName = user.getUserName();
        final long userID = user.getUserId();
        final String authToken = Constants.BASIC +
                user.getAuthenticationKey();

        preferencesHelper.saveUsername(userName);
        preferencesHelper.setUserId(userID);
        preferencesHelper.saveToken(authToken);

        FineractApiManager.createService(preferencesHelper.getToken());

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
