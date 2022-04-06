package org.mifos.mobilewallet.mifospay.password;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 27/June/2018
 */

public interface EditPasswordContract {

    interface EditPasswordPresenter extends BasePresenter {

        void updatePassword(String currentPassword, String newPassword, String newPasswordRepeat);

        void handleSavePasswordButtonStatus(String currentPassword,
                                            String newPassword,
                                            String newPasswordRepeat);
    }

    interface EditPasswordView extends BaseView<EditPasswordPresenter> {

        void closeActivity();

        void startProgressBar();

        void stopProgressBar();

        void showError(String msg);

        void enableSavePasswordButton();

        void disableSavePasswordButton();
    }
}
