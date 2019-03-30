package org.mifos.mobilewallet.mifospay.auth;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * Created by naman on 16/6/17.
 */

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AuthContract {

    interface LoginView extends BaseView<LoginPresenter> {

        void disableLoginButton();

        void enableLoginButton();

        void loginSuccess();

        void loginFail(String message);
    }

    interface LoginPresenter extends BasePresenter {

        void handleLoginButtonStatus(String usernameContent, String passwordContent);

        void loginUser(String username, String password);
    }
}
