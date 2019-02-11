package org.mifos.mobilewallet.mifospay.registration.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;

import javax.inject.Inject;

/**
 * This class contains the components of the Presenter required for Mobile Verification.
 * @author ankur
 * @since 21/June/2018
 */

public class MobileVerificationPresenter implements
        RegistrationContract.MobileVerificationPresenter {

    private final UseCaseHandler mUseCaseHandler;
    RegistrationContract.MobileVerificationView mMobileVerificationView;
    @Inject
    SearchClient searchClientUseCase;

    @Inject
    public MobileVerificationPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    /**
     * This function attaches a view.
     * @param baseView This view is set as Mobile Verification View.
     */
    @Override
    public void attachView(BaseView baseView) {
        mMobileVerificationView = (RegistrationContract.MobileVerificationView) baseView;
        mMobileVerificationView.setPresenter(this);
    }

    /**
     * This function requests for the OTP from the server.
     * @param fullNumber This is the full number.
     * @param mobileNo This is the mobile number of the user
     */
    @Override
    public void requestOTPfromServer(String fullNumber, String mobileNo) {

        mUseCaseHandler.execute(searchClientUseCase, new SearchClient.RequestValues(mobileNo),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
                    @Override
                    public void onSuccess(SearchClient.ResponseValue response) {
                        mMobileVerificationView.onRequestOtpFailed("Mobile number already exists.");
                    }

                    @Override
                    public void onError(String message) {
                        // TODO:: request OTP
                        mMobileVerificationView.onRequestOtpSuccess();
                    }
                });
    }

    /**
     * This function verifies the OTP.
     * @param otp The otp which was received by the user
     */
    @Override
    public void verifyOTP(String otp) {
        // TODO:: verify OTP
        mMobileVerificationView.onOtpVerificationSuccess();

        // TODO::

//        if (false) { // on error
//            mMobileVerificationView.onOtpVerificationFailed("OTP Verification Failed.");
//        }
    }
}
