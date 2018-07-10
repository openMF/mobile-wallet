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

}
