package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.domain.model.User;
import mifos.org.mobilewallet.core.domain.usecase.AuthenticateUser;

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
