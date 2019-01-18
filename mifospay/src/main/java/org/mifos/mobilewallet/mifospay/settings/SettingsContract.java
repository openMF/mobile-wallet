package org.mifos.mobilewallet.mifospay.settings;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Interface for settings presenter and view
 * @author ankur
 * @since 9/7/18
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

