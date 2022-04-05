package org.mifos.mobilewallet.mifospay.editprofile;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 27/June/2018
 */

public interface EditProfileContract {

    interface EditProfilePresenter extends BasePresenter {

        void fetchUserDetails();

        void handleNecessaryDataSave();

        void updateInputById(int id, String content);

        void updateEmail(String email);

        void updateMobile(String fullNumber);

        void handleProfileImageChangeRequest();

        void handleProfileImageRemoved();

        void handleClickProfileImageRequest();

        void handleExitOnUnsavedChanges();

        void onDialogNegative();

        void onDialogPositive();
    }

    interface EditProfileView extends BaseView<EditProfilePresenter> {

        void showDefaultImageByUsername(String fullName);

        void showUsername(String username);

        void showEmail(String email);

        void showVpa(String vpa);

        void showMobileNumber(String mobileNumber);

        void removeProfileImage();

        void changeProfileImage();

        void clickProfileImage();

        void onUpdateEmailError(String message);

        void onUpdateMobileError(String message);

        void showToast(String message);

        void showFab();

        void hideFab();

        void hideKeyboard();

        void startProgressBar();

        void stopProgressBar();

        void showDiscardChangesDialog();

        void closeActivity();
    }
}
