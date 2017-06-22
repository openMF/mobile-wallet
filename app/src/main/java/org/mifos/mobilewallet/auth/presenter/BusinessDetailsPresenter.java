package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.core.UseCaseHandler;

import javax.inject.Inject;

/**
 * Created by naman on 20/6/17.
 */

public class BusinessDetailsPresenter implements AuthContract.BusinessDetailsPresenter {

    private AuthContract.BusinessDetailsView mBusinessDetailsView;
    private final UseCaseHandler mUsecaseHandler;


    @Inject
    public BusinessDetailsPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        this.mBusinessDetailsView = (AuthContract.BusinessDetailsView) baseView;
        mBusinessDetailsView.setPresenter(this);

    }

    @Override
    public void registerDetails() {
        mBusinessDetailsView.openAddAccount();
    }
}
