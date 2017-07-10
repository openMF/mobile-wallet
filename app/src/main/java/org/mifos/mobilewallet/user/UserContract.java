package org.mifos.mobilewallet.user;

import org.mifos.mobilewallet.core.BasePresenter;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;

/**
 * Created by naman on 22/6/17.
 */

public interface UserContract {

    interface  UserDetailsView extends BaseView<UserDetailsPresenter> {

        void showUserDetails(ClientDetails clientDetails);
        void showPanStatus(boolean status);
    }

    interface UserDetailsPresenter extends BasePresenter {

        void getUserDetails();
        void verifyPan(String number);
    }
}
