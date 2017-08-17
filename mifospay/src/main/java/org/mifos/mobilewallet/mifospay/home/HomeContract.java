package org.mifos.mobilewallet.mifospay.home;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import mifos.org.mobilewallet.core.domain.model.ClientDetails;

/**
 * Created by naman on 17/6/17.
 */

public interface HomeContract {

    interface HomeView extends BaseView<HomePresenter> {

        void showUserDetailsHeader(ClientDetails clientDetails);
    }

    interface HomePresenter extends BasePresenter {

        void fetchClientDetails();
    }


    interface WalletView extends BaseView<WalletPresenter> {


    }

    interface WalletPresenter extends BasePresenter {


    }

}
