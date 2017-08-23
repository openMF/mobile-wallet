package org.mifos.mobilewallet.home;

import org.mifos.mobilewallet.base.BasePresenter;
import org.mifos.mobilewallet.base.BaseView;

import org.mifos.mobilewallet.core.domain.model.ClientDetails;

/**
 * Created by naman on 17/6/17.
 */

public interface HomeContract {

    interface HomeView extends BaseView<HomePresenter> {

        void showClientDetails(ClientDetails clientDetails);
        void showWalletBalance(int amount);
    }

    interface HomePresenter extends BasePresenter {

        void fetchClientDetails();
        void fetchWalletBalance();
    }
}
