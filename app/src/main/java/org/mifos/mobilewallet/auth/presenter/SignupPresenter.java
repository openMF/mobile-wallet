package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.CreateUser;

/**
 * Created by naman on 16/6/17.
 */

public class SignupPresenter implements AuthContract.SignupPresenter {

    private AuthContract.SignupView mSignupView;
    private final UseCaseHandler mUsecaseHandler;


    @Inject
    CreateUser createUser;

    @Inject
    public SignupPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        this.mSignupView = (AuthContract.SignupView) baseView;
        mSignupView.setPresenter(this);

    }

    @Override
    public void onVerifyNumber() {
        mSignupView.openAddDetails();
    }

    @Override
    public void navigateLogin() {
        mSignupView.openLoginScreen();
    }
}
