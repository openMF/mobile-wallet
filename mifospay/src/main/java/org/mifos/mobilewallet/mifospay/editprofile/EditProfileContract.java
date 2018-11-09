package org.mifos.mobilewallet.mifospay.editprofile;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 27/June/2018
 */

public interface EditProfileContract {

    interface EditProfilePresenter extends BasePresenter {

        void updatePassword(String currentPassword, String newPassword);

        void updatePasscode(String currentPasscode, String newPasscode);

        void updateEmail(String email);

        void updateMobile(String fullNumber);

        void handleProfileImageChangeRequest();

        void handleProfileImageRemoved();

        void fetchUserDetails();
    }

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
