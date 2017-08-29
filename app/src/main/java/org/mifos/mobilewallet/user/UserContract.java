package org.mifos.mobilewallet.user;

import org.mifos.mobilewallet.base.BasePresenter;
import org.mifos.mobilewallet.base.BaseView;

import org.mifos.mobilewallet.core.domain.model.Client;

/**
 * Created by naman on 22/6/17.
 */

public interface UserContract {

    interface  UserDetailsView extends BaseView<UserDetailsPresenter> {

        void showUserDetails(Client client);
        void showPanStatus(boolean status);
    }

    interface UserDetailsPresenter extends BasePresenter {

        void getUserDetails();
        void verifyPan(String number);
    }
}
