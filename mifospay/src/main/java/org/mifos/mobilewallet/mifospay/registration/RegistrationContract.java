package org.mifos.mobilewallet.mifospay.registration;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 21/June/2018
 */

public interface RegistrationContract {

    interface MobileVerificationView extends BaseView<MobileVerificationPresenter> {

        void onRequestOtpSuccess();

        void onOtpVerificationSuccess();

        void showToast(String s);

        void hideProgressDialog();

        void onRequestOtpFailed(String s);

        void onOtpVerificationFailed(String s);
    }

    interface MobileVerificationPresenter extends BasePresenter {

        void requestOTPfromServer(String fullNumber, String s);

        void verifyOTP(String otp);
    }


    interface SignupView extends BaseView<SignupPresenter> {

        void showToast(String s);

        void hideProgressDialog();

        void loginSuccess();

        void onRegisterFailed(String message);

        void onRegisterSuccess(String s);

        void updatePasswordStrength(int stringRes, int colorRes, int value);
    }

    interface SignupPresenter extends BasePresenter {

        void checkPasswordStrength(String password);

        void registerUser(String firstName, String lastName, String mobileNumber, String email,
                String businessName, String addressline1, String addressline2, String pincode,
                String city, String countryName, String username, String password,
                String stateId, String countryId, int mifosSavingProductId);
    }
}
