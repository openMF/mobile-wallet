package org.mifos.mobilewallet.mifospay.bank;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * Created by ankur on 09/July/2018
 */

public interface BankContract {

    interface BankAccountsPresenter extends BasePresenter {

        void fetchLinkedBankAccounts();
    }

    interface BankAccountsView extends BaseView<BankAccountsPresenter> {

        void showLinkedBankAccounts(List<BankAccountDetails> bankAccountList);
    }

    interface LinkBankAccountPresenter extends BasePresenter {

        void fetchBankAccountDetails(String bankName);
    }

    interface LinkBankAccountView extends BaseView<LinkBankAccountPresenter> {

        void addBankAccount(BankAccountDetails bankAccountDetails);
    }

    interface BankAccountDetailPresenter extends BasePresenter {

    }

    interface BankAccountDetailView extends BaseView<BankAccountDetailPresenter> {

    }


    interface DebitCardPresenter extends BasePresenter {

        void verifyDebitCard(String s, String s1, String s2);
    }

    interface DebitCardView extends BaseView<DebitCardPresenter> {

        void verifyDebitCardSuccess(String otp);

        void verifyDebitCardError(String message, int viewNumber);
    }

    interface UpiPinPresenter extends BasePresenter {

    }

    interface UpiPinView extends BaseView<UpiPinPresenter> {

    }

    interface SetupUpiPinPresenter extends BasePresenter {

        void setupUpiPin(BankAccountDetails bankAccountDetails, String upiPin);

        void requestOtp(BankAccountDetails bankAccountDetails);
    }

    interface SetupUpiPinView extends BaseView<SetupUpiPinPresenter> {

        void debitCardVerified(String otp);

        void setupUpiPinSuccess(String mSetupUpiPin);

        void setupUpiPinError(String message);
    }

}
