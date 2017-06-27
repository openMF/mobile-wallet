package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.core.UseCaseHandler;
import org.mifos.mobilewallet.user.domain.usecase.VerifyPanDetails;

import javax.inject.Inject;

/**
 * Created by naman on 20/6/17.
 */

public class BusinessDetailsPresenter implements AuthContract.BusinessDetailsPresenter {

    private AuthContract.BusinessDetailsView mBusinessDetailsView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    VerifyPanDetails verifyPanDetails;

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

    @Override
    public void verifyPan(String number) {
        mUsecaseHandler.execute(verifyPanDetails, new VerifyPanDetails.RequestValues(number),
                new UseCase.UseCaseCallback<VerifyPanDetails.ResponseValue>() {
                    @Override
                    public void onSuccess(VerifyPanDetails.ResponseValue response) {
                        mBusinessDetailsView.showPanStatus(response.isStatus());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
