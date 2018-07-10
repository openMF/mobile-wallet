package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.user.CreateUser;

import javax.inject.Inject;

/**
 * Created by naman on 16/6/17.
 */

public class SignupPresenter implements AuthContract.SignupPresenter {

    private final UseCaseHandler mUsecaseHandler;
    @Inject
    CreateUser createUser;
    private AuthContract.SignupView mSignupView;

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
