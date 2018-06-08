package org.mifos.mobilewallet.mifospay.transactions;

import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * Created by naman on 17/8/17.
 */

public interface TransactionsContract {

    interface TransactionsHistoryView extends BaseView<TransactionsHistoryPresenter> {

        void showTransactions(List<Transaction> transactions);

        void showTransactionDetailDialog(
                Transaction transaction);

        void showSnackbar(String message);
    }

    interface TransactionsHistoryPresenter extends BasePresenter {

        void fetchTransactions(long accountId);

        void fetchTransfer(Transaction transaction);
    }

    interface TransactionDetailView extends BaseView<TransactionDetailPresenter> {

    }

    interface TransactionDetailPresenter extends BasePresenter {

    }

    interface SpecificTransactionsView extends BaseView<SpecificTransatcionsPresenter> {

    }

    interface SpecificTransatcionsPresenter extends BasePresenter {

    }


}
