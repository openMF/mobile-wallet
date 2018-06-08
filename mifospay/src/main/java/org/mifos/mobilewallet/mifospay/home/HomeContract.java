package org.mifos.mobilewallet.mifospay.home;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.Client;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

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

        void showWallet(Account account);

    }

    interface WalletPresenter extends BasePresenter {

        void fetchWallet();

    }


    interface TransferView extends BaseView<TransferPresenter> {

        void showVpa(String vpa);
    }

    interface TransferPresenter extends BasePresenter {

        void fetchVpa();
    }

    interface ProfileView extends BaseView<ProfilePresenter> {

        void showProfile(Client client);

    }

    interface ProfilePresenter extends BasePresenter {

        void fetchprofile();

    }


}
