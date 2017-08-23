package org.mifos.mobilewallet.account;

import org.mifos.mobilewallet.base.BasePresenter;
import org.mifos.mobilewallet.base.BaseView;

import java.util.List;

import org.mifos.mobilewallet.core.domain.model.Account;

/**
 * Created by naman on 11/7/17.
 */

public interface AccountContract {

    interface AccountsView extends BaseView<AccountsPresenter> {

        void showAccounts(List<Account> accounts);
        void showError(String message);
    }

    interface AccountsPresenter extends BasePresenter {

        void fetchAccounts();
    }
}
