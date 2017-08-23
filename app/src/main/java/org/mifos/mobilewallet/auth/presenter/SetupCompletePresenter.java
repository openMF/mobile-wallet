package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCaseHandler;

/**
 * Created by naman on 20/6/17.
 */

public class SetupCompletePresenter implements AuthContract.SetupCompletePresenter {

    private AuthContract.SetupCompleteView mSetupCompleteView;
    private final UseCaseHandler mUsecaseHandler;


    @Inject
    public SetupCompletePresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        this.mSetupCompleteView = (AuthContract.SetupCompleteView) baseView;
        mSetupCompleteView.setPresenter(this);

    }

    @Override
    public void navigateHome() {
        mSetupCompleteView.openHome();
    }
}
