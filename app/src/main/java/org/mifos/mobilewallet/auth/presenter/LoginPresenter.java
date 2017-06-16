package org.mifos.mobilewallet.auth.presenter;

import android.util.Log;

import org.mifos.mobilewallet.auth.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.auth.AuthContract;
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
    public LoginPresenter(UseCaseHandler useCaseHandler){
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mLoginView = (AuthContract.LoginView) baseView;
        mLoginView.setPresenter(this);
    }


    public void authenticateUser(AuthenticateUser authenticateUser) {

        AuthenticateUser.RequestValues requestValue = authenticateUser.getRequestValues();

        mUsecaseHandler.execute(authenticateUser, requestValue, new UseCase.UseCaseCallback<AuthenticateUser.ResponseValue>() {
            @Override
            public void onSuccess(AuthenticateUser.ResponseValue response) {
                Log.e("lol","login success");
            }

            @Override
            public void onError() {

            }
        });
    }
}
