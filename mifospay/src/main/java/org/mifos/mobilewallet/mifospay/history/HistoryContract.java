package org.mifos.mobilewallet.mifospay.history;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;
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

        void showTransferDetail(TransferDetail transferDetail);

        void showProgressBar();

        void hideProgressBar();

        void showToast(String message);
    }

    interface TransactionDetailPresenter extends BasePresenter {

        void getTransferDetail(long transferId);
    }

    interface SpecificTransactionsView extends BaseView<SpecificTransactionsPresenter> {

        void showSpecificTransactions(ArrayList<Transaction> specificTransactions);

        void showProgress();

        void hideProgress();

        void showStateView(int drawable, int title, int subtitle);
    }

    interface SpecificTransactionsPresenter extends BasePresenter {

        ArrayList<Transaction> getSpecificTransactions(ArrayList<Transaction> transactions,
                                                       String secondAccountNumber);
    }


}
