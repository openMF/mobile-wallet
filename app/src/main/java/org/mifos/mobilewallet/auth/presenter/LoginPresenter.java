package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.domain.model.User;
import org.mifos.mobilewallet.auth.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.core.UseCaseHandler;

import javax.inject.Inject;

/**
 * Created by naman on 16/6/17.
 */

public class LoginPresenter implements AuthContract.LoginPresenter {

    private AuthContract.LoginView mLoginView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    AuthenticateUser authenticateUser;

    @Inject
    public LoginPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mLoginView = (AuthContract.LoginView) baseView;
        mLoginView.setPresenter(this);
    }


    public void loginUser(String username, String password) {

        authenticateUser.setRequestValues(new AuthenticateUser.RequestValues(username, password));
        AuthenticateUser.RequestValues requestValue = authenticateUser.getRequestValues();

        mUsecaseHandler.execute(authenticateUser, requestValue,
                new UseCase.UseCaseCallback<AuthenticateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(AuthenticateUser.ResponseValue response) {
                        User user = response.getUser();
                        mLoginView.loginSuccess();
                    }

                    @Override
                    public void onError(String message) {
                        mLoginView.loginFail(message);
                    }
                });
    }
}
