package org.mifos.mobilewallet.mifospay.history;

import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 17/8/17.
 */

public interface HistoryContract {

    interface TransactionsHistoryAsync {

        void onTransactionsFetchCompleted(List<JournalEntry> transactions);

    }

    interface HistoryView extends BaseView<TransactionsHistoryPresenter> {

        void showRecyclerView();

        void showStateView(int drawable, int title, int subtitle);

        void showTransactions(List<JournalEntry> transactions);

        void showTransactionDetailDialog(int transactionIndex);

        void showHistoryFetchingProgress();
    }

    interface TransactionsHistoryPresenter extends BasePresenter {

        void fetchTransactions(String accountIdentifier);

    }

    interface TransactionDetailView extends BaseView<TransactionDetailPresenter> {

        void showCustomerName(String customerName);

        void showProgressBar();

        void hideProgressBar();

        void showError(String message);

    }

    interface TransactionDetailPresenter extends BasePresenter {

        void fetchAccountDetail(String accountIdentifier);

    }

    interface SpecificTransactionsView extends BaseView<SpecificTransactionsPresenter> {

        void showSpecificTransactions(ArrayList<JournalEntry> specificTransactions);

        void showProgress();

        void hideProgress();

        void showStateView(int drawable, int title, int subtitle);
    }

    interface SpecificTransactionsPresenter extends BasePresenter {

        void getSpecificTransactions(ArrayList<JournalEntry> transactions,
                                                       String secondAccountIdentifier);
    }


}
