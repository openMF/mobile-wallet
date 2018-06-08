package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.core.base.UseCaseHandler;

import javax.inject.Inject;

/**
 * Created by naman on 20/6/17.
 */

public class BankAccountPresenter implements AuthContract.BankAccountPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private AuthContract.BankAccountView mAccountView;


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
