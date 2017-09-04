package org.mifos.mobilewallet.mifospay.wallet;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

import org.mifos.mobilewallet.core.domain.model.Transaction;

/**
 * Created by naman on 17/8/17.
 */

public interface WalletContract {

    interface WalletDetailView extends BaseView<WalletContract.WalletDetailPresenter> {

        void showWalletTransactions(List<Transaction> transactions);
    }

    interface WalletDetailPresenter extends BasePresenter {

        void fetchWalletTransactions(long accountId);
    }

}
