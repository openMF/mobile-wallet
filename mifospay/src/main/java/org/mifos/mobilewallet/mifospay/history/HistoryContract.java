package org.mifos.mobilewallet.mifospay.history;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 17/8/17.
 */

public interface HistoryContract {

    interface TransactionsHistoryAsync {

        void onTransactionsFetchCompleted(List<Transaction> transactions);

    }

    interface HistoryView extends BaseView<TransactionsHistoryPresenter> {

        void showRecyclerView();

        void showStateView(int drawable, int title, int subtitle);

        void showTransactions(List<Transaction> transactions);

        void showTransactionDetailDialog(int transactionIndex, String accountNumber);

        void showHistoryFetchingProgress();
    }

    interface TransactionsHistoryPresenter extends BasePresenter {

        void fetchTransactions();

        void handleTransactionClick(int transactionIndex);

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
