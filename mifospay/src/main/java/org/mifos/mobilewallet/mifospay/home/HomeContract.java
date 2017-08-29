package org.mifos.mobilewallet.mifospay.home;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.Client;

/**
 * Created by naman on 17/6/17.
 */

public interface HomeContract {

    interface HomeView extends BaseView<HomePresenter> {

        void showClientDetails(Client client);
    }

    interface HomePresenter extends BasePresenter {

        void fetchClientDetails();
    }


    interface WalletView extends BaseView<WalletPresenter> {

        void showWallets(List<Account> accounts);

    }

    interface WalletPresenter extends BasePresenter {

        void fetchWallets();

    }

}
