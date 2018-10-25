package org.mifos.mobilewallet.mifospay.settings;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by ankur on 09/July/2018
 */

public interface SettingsContract {

    interface SettingsPresenter extends BasePresenter {

        void logout();

        void disableAccount();
    }

    interface SettingsView extends BaseView<SettingsPresenter> {

        void startLoginActivity();
    }
}
