package org.mifos.mobilewallet.mifospay.home;

import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by naman on 17/6/17.
 */

public interface BaseHomeContract {

    interface BaseHomeView extends BaseView<BaseHomePresenter> {

        void showClientDetails(Client client);
    }

    interface BaseHomePresenter extends BasePresenter {

        void fetchClientDetails();
    }

    interface HomeView extends BaseView<HomePresenter> {

        void showSnackbar(String message);

        void setAccountBalance(Account account);

        void showTransactionsHistory(List<Transaction> transactions);

        void showTransactionsError();

        void showTransactionsEmpty();

        void showBottomSheetActionButton();

        void hideBottomSheetActionButton();

        void showToast(String message);

        void hideSwipeProgress();

        void hideTransactionLoading();
    }

    interface HomePresenter extends BasePresenter {

        void fetchAccountDetails();

        void showMoreHistory(int existingItemsCount);

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

        boolean checkSelfTransfer(String externalId);

        void fetchMobile();

        void checkBalanceAvailability(String externalId, double transferAmount);
    }

    interface MerchantTransferView extends BaseView<MerchantTransferPresenter> {

        void showToast(String message);

        void hideSwipeProgress();

        void showPaymentDetails(String externalId, double amount);

        void showTransactionFetching();

        void showTransactions(List<Transaction> transactions);

        void showSpecificView(int drawable, int title, int subtitle);

    }

    interface MerchantTransferPresenter extends BasePresenter {

        void checkBalanceAvailability(String externalId, double transferAmount);

        void fetchMerchantTransfers(String merchantAccountNo);

    }

    interface ProfileView extends BaseView<ProfilePresenter> {

        void showProfile(Client client);

        void showEmail(String email);

        void showVpa(String vpa);

        void showMobile(String mobile);

        void showToast(String message);

        void showSnackbar(String message);

        void fetchImageSuccess(ResponseBody responseBody);
    }

    interface ProfilePresenter extends BasePresenter {

        void fetchProfile();

        void fetchAccountDetails();

        void fetchClientImage();
    }


}
