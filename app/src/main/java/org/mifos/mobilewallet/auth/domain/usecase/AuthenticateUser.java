package org.mifos.mobilewallet.auth.domain.usecase;

import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.data.local.PreferencesHelper;
import org.mifos.mobilewallet.utils.Constants;

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
    private final PreferencesHelper preferencesHelper;

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
