package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCaseHandler;

/**
 * Created by naman on 20/6/17.
 */

public class BankAccountPresenter implements AuthContract.BankAccountPresenter {

    private AuthContract.BankAccountView mAccountView;
    private final UseCaseHandler mUsecaseHandler;


    @Inject
    public BankAccountPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        this.mAccountView = (AuthContract.BankAccountView) baseView;
        mAccountView.setPresenter(this);

    }

    @Override
    public void setUPIPin() {
        mAccountView.setupComplete();
    }
}
