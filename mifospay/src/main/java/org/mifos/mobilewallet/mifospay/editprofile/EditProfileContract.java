package org.mifos.mobilewallet.mifospay.editprofile;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * This is a contract class working as an Interface for UI
 * and Presenter components of the EditProfile package.
 * @author ankur
 * @since 27/June/2018
 */

public interface EditProfileContract {

    /**
     * Defines all the functions in Presenter Component.
     */
    interface EditProfilePresenter extends BasePresenter {

        void updatePassword(String currentPassword, String newPassword);

        void updatePasscode(String currentPasscode, String newPasscode);

        void updateEmail(String email);

        void updateMobile(String fullNumber);

        void handleProfileImageChangeRequest();

        void handleProfileImageRemoved();

        void fetchUserDetails();
    }

    /**
     * Defines all the functions in UI Component.
     */
    interface EditProfileView extends BaseView<EditProfilePresenter> {

        void showToast(String message);

        void onUpdateEmailSuccess(String email);

        void onUpdateEmailError(String message);

        void onUpdateMobileSuccess(String fullNumber);

        void onUpdateMobileError(String message);

        void onUpdatePasswordSuccess();

        void onUpdatePasswordError(String message);

        void changeProfileImage();

        void removeProfileImage();

        void setEmail(String email);

        void setMobile(String mobile);

        void setImage(String fullName);
    }
}
