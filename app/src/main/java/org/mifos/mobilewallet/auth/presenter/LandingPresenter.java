package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.base.UseCaseHandler;

import javax.inject.Inject;

/**
 * Created by naman on 16/6/17.
 */

public class LandingPresenter implements AuthContract.LandingPresenter {

    private AuthContract.LandingView mLandingView;
    private final UseCaseHandler mUsecaseHandler;


    @Inject
    public LandingPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        this.mLandingView = (AuthContract.LandingView) baseView;
        mLandingView.setPresenter(this);

    }


    @Override
    public void navigateLogin() {
        mLandingView.openLoginScreen();
    }

    @Override
    public void navigateSignup() {
        mLandingView.openSignupScreen();
    }
}
