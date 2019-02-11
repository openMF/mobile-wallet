package org.mifos.mobilewallet.mifospay.registration;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * A contract class working as an interface between the UI and the presenter of the Registration
 * @author ankur
 * @since 21/June/2018
 */

public interface RegistrationContract {
    /**
     * Contains all the functions of the UI component of Mobile Verification View.
     */
    interface MobileVerificationView extends BaseView<MobileVerificationPresenter> {

        void onRequestOtpSuccess();

        void onOtpVerificationSuccess();

        void showToast(String s);

        void hideProgressDialog();

        void onRequestOtpFailed(String s);

        void onOtpVerificationFailed(String s);
    }

    /**
     * Contains all the functions of the Presenter component of Mobile Verification View.
     */
    interface MobileVerificationPresenter extends BasePresenter {

        void requestOTPfromServer(String fullNumber, String s);

        void verifyOTP(String otp);
    }

    /**
     * Contains all the functions of the UI component of SignUp View
     */
    interface SignupView extends BaseView<SignupPresenter> {

        void showToast(String s);

        void hideProgressDialog();

        void loginSuccess();

        void onRegisterFailed(String message);

        void onRegisterSuccess(String s);
    }

    /**
     * Contains all the functions of the Presenter component of the SignUp View.
     */
    interface SignupPresenter extends BasePresenter {

        void registerUser(String firstName, String lastName, String mobileNumber, String email,
                String businessName, String addressline1, String addressline2, String pincode,
                String city, String countryName, String username, String password,
                String stateId, String countryId, int mifosSavingProductId);
    }
}
