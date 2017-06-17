package org.mifos.mobilewallet.auth;

import org.mifos.mobilewallet.auth.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.core.BasePresenter;
import org.mifos.mobilewallet.core.BaseView;

/**
 * Created by naman on 16/6/17.
 */

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AuthContract {

    interface LoginView extends BaseView<LoginPresenter> {

    }

    interface LoginPresenter extends BasePresenter {

        void authenticateUser(String username, String password);
    }


    interface SignupView extends BaseView<SignupPresenter> {

    }

    interface SignupPresenter extends BasePresenter {


    }


    interface LandingView extends BaseView<LandingPresenter> {

        void openLoginScreen();
        void openSignupScreen();
    }

    interface LandingPresenter extends BasePresenter {

        void navigateLogin();
        void navigateSignup();

    }
}
