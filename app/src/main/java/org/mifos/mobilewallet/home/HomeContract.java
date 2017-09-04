package org.mifos.mobilewallet.home;

import org.mifos.mobilewallet.base.BasePresenter;
import org.mifos.mobilewallet.base.BaseView;

import org.mifos.mobilewallet.core.domain.model.Client;

/**
 * Created by naman on 17/6/17.
 */

public interface HomeContract {

    interface HomeView extends BaseView<HomePresenter> {

        void showClientDetails(Client client);
        void showWalletBalance(int amount);
    }

    interface HomePresenter extends BasePresenter {

        void fetchClientDetails();
        void fetchWalletBalance();
    }
}
