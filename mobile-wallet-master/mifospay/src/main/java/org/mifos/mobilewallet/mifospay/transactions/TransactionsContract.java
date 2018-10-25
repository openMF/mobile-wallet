package org.mifos.mobilewallet.mifospay.transactions;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 17/8/17.
 */

public interface TransactionsContract {

    interface TransactionsHistoryView extends BaseView<TransactionsHistoryPresenter> {

        void showTransactions(List<Transaction> transactions);

        void showSnackbar(String message);

        void showToast(String error_fetching_transactions);

        void hideSwipeProgress();
    }

    interface TransactionsHistoryPresenter extends BasePresenter {

        void fetchTransactions(long accountId);

    }

    interface TransactionDetailView extends BaseView<TransactionDetailPresenter> {

    }

    interface TransactionDetailPresenter extends BasePresenter {

        ArrayList<Transaction> getSpecificTransactions(ArrayList<Transaction> transactions,
                String secondAccountNumber);
    }

    interface SpecificTransactionsView extends BaseView<SpecificTransactionsPresenter> {

    }

    interface SpecificTransactionsPresenter extends BasePresenter {

    }


}
