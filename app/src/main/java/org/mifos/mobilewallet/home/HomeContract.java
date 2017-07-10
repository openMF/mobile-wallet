package org.mifos.mobilewallet.home;

import org.mifos.mobilewallet.core.BasePresenter;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;

/**
 * Created by naman on 17/6/17.
 */

public interface HomeContract {

    interface HomeView extends BaseView<HomePresenter> {

        void showUserDetailsHeader(ClientDetails clientDetails);
        void showWalletBalance(int amount);
    }

    interface HomePresenter extends BasePresenter {

        void fetchClientDetails();
        void fetchWalletBalance();
    }
}
