package org.mifos.mobilewallet.mifospay.home;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import okhttp3.ResponseBody;

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

        void showSnackbar(String message);

        void showWallet(Account account);

        void showToast(String message);

        void hideSwipeProgress();
    }

    interface WalletPresenter extends BasePresenter {

        void fetchWallet();

    }


    interface TransferView extends BaseView<TransferPresenter> {

        void showVpa(String vpa);

        void showToast(String message);

        void showSnackbar(String message);

        void showMobile(String mobileNo);

        void hideSwipeProgress();

        void showClientDetails(String externalId, double amount);
    }

    interface TransferPresenter extends BasePresenter {

        void fetchVpa();

        void fetchMobile();

        void checkBalanceAvailability(String externalId, double transferAmount);
    }

    interface ProfileView extends BaseView<ProfilePresenter> {

        void showProfile(Client client);

        void showToast(String message);

        void showSnackbar(String message);

        void fetchImageSuccess(ResponseBody responseBody);
    }

    interface ProfilePresenter extends BasePresenter {

        void fetchprofile();

        void fetchClientImage();
    }


}
