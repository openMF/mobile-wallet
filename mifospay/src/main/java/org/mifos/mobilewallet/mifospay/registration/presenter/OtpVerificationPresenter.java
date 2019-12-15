package org.mifos.mobilewallet.mifospay.registration.presenter;

import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;

import javax.inject.Inject;

public class OtpVerificationPresenter implements RegistrationContract.OtpVerificationPresenter {
    RegistrationContract.OtpVerificationView mOtpVerificationView;

    @Inject
    public OtpVerificationPresenter() {
    }

    @Override
    public void verifyOTP(String otp) {
        // TODO:: verify OTP
        mOtpVerificationView.onOtpVerificationSuccess();

        // TODO::

        //if (false) { // on error
        //mOtpVerificationView.onOtpVerificationFailed("OTP Verification Failed.");
        //}
    }

    @Override
    public void attachView(BaseView baseView) {
        mOtpVerificationView = (RegistrationContract.OtpVerificationView) baseView;
        mOtpVerificationView.setPresenter(this);
    }
}
