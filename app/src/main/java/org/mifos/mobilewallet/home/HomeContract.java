package org.mifos.mobilewallet.home;

import org.mifos.mobilewallet.core.BasePresenter;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.home.domain.model.UserDetails;

/**
 * Created by naman on 17/6/17.
 */

public interface HomeContract {

    interface HomeView extends BaseView<HomePresenter> {

        void showUserDetailsHeader(UserDetails userDetails);
        void showWalletBalance(int amount);
    }

    interface HomePresenter extends BasePresenter {

        void fetchUserData();
        void fetchWalletBalance();
    }
}
