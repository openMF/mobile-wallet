package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.domain.usecase.VerifyAadharDetails;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.user.domain.usecase.VerifyPanDetails;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;

/**
 * Created by naman on 20/6/17.
 */

public class BusinessDetailsPresenter implements AuthContract.BusinessDetailsPresenter {

    private AuthContract.BusinessDetailsView mBusinessDetailsView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    VerifyPanDetails verifyPanDetails;

    @Inject
    VerifyAadharDetails verifyAadharDetails;

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

    @Override
    public void verifyAadhar(String number) {
        mUsecaseHandler.execute(verifyAadharDetails, new VerifyAadharDetails.RequestValues(number,
                        0),
                new UseCase.UseCaseCallback<VerifyAadharDetails.ResponseValue>() {
                    @Override
                    public void onSuccess(VerifyAadharDetails.ResponseValue response) {
                        mBusinessDetailsView.showAadharValid(response.isStatus());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    @Override
    public void generateAadharOtp() {
        mUsecaseHandler.execute(verifyAadharDetails, new VerifyAadharDetails.RequestValues("", 1),
                new UseCase.UseCaseCallback<VerifyAadharDetails.ResponseValue>() {
                    @Override
                    public void onSuccess(VerifyAadharDetails.ResponseValue response) {
                        mBusinessDetailsView.showAadharOtpSent();
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    @Override
    public void verifyAadharOtp(String otp) {
        mUsecaseHandler.execute(verifyAadharDetails, new VerifyAadharDetails.RequestValues(otp, 2),
                new UseCase.UseCaseCallback<VerifyAadharDetails.ResponseValue>() {
                    @Override
                    public void onSuccess(VerifyAadharDetails.ResponseValue response) {
                        mBusinessDetailsView.showAadharStatus(response.isStatus());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
