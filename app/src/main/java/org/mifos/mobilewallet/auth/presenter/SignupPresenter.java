package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.domain.model.NewUser;
import org.mifos.mobilewallet.auth.domain.usecase.CreateUser;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.core.UseCaseHandler;

import javax.inject.Inject;

/**
 * Created by naman on 16/6/17.
 */

public class SignupPresenter implements AuthContract.SignupPresenter {

    private AuthContract.SignupView mSignupView;
    private final UseCaseHandler mUsecaseHandler;


    @Inject
    CreateUser createUser;

    @Inject
    public SignupPresenter(UseCaseHandler useCaseHandler){
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
