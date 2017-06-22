package org.mifos.mobilewallet.auth;

import org.mifos.mobilewallet.auth.domain.model.Bank;
import org.mifos.mobilewallet.auth.domain.model.NewUser;
import org.mifos.mobilewallet.auth.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.core.BasePresenter;
import org.mifos.mobilewallet.core.BaseView;

import java.util.List;

/**
 * Created by naman on 16/6/17.
 */

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AuthContract {

    interface LoginView extends BaseView<LoginPresenter> {

        void loginSuccess();
        void loginFail(String message);
    }

    interface LoginPresenter extends BasePresenter {

        void authenticateUser(String username, String password);
    }


    interface SignupView extends BaseView<SignupPresenter> {

        void openAddDetails();
        void openLoginScreen();
    }

    interface SignupPresenter extends BasePresenter {

        void onVerifyNumber();
        void navigateLogin();

    }


    interface LandingView extends BaseView<LandingPresenter> {

        void openLoginScreen();
        void openSignupScreen();
    }

    interface LandingPresenter extends BasePresenter {

        void navigateLogin();
        void navigateSignup();

    }

    interface AddAccountView extends BaseView<AddAcountPresenter> {

        void showBanks(List<Bank> popularBanks, List<Bank> otherBanks);
        void openBankAccount();
    }

    interface AddAcountPresenter extends BasePresenter {

        void loadBankData();
        void bankSelected(Bank bank);
    }

    interface BusinessDetailsView extends BaseView<BusinessDetailsPresenter> {

        void openAddAccount();
    }

    interface BusinessDetailsPresenter extends BasePresenter {

        void registerDetails();
    }

    interface BankAccountView extends BaseView<BankAccountPresenter> {

        void setupComplete();
    }

    interface BankAccountPresenter extends BasePresenter {

        void setUPIPin();
    }

    interface SetupCompleteView extends BaseView<SetupCompletePresenter> {

        void openHome();
    }

    interface SetupCompletePresenter extends BasePresenter {

        void navigateHome();
    }
}
