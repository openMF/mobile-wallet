package org.mifos.mobilewallet.mifospay.auth;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * This is an interface which specifies the contract
 * between view and the presenter.
 * @author naman
 * @since 16-June-17.
 */


public interface AuthContract {

    /**
     * Defines all the functions in UI Component.
     */
    interface LoginView extends BaseView<LoginPresenter> {

        void loginSuccess();

        void loginFail(String message);
    }

    /**
     * Defines all the functions in presenter component
     */
    interface LoginPresenter extends BasePresenter {

        void loginUser(String username, String password);
    }
}
