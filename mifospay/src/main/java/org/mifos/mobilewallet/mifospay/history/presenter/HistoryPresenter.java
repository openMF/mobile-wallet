package org.mifos.mobilewallet.mifospay.history.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineractcn.entity.journal.JournalEntry;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory;
import java.util.List;
import javax.inject.Inject;

public class HistoryPresenter implements HistoryContract.TransactionsHistoryPresenter,
        HistoryContract.TransactionsHistoryAsync {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    @Inject
    TransactionsHistory mTransactionsHistory;
    private HistoryContract.HistoryView mHistoryView;

    @Inject
    public HistoryPresenter(UseCaseHandler useCaseHandler, LocalRepository localRepository) {
        this.mUseCaseHandler = useCaseHandler;
        this.mLocalRepository = localRepository;
    }

    @Override
    public void attachView(BaseView baseView) {
        mHistoryView = (HistoryContract.HistoryView) baseView;
        mHistoryView.setPresenter(this);
        mTransactionsHistory.delegate = this;
    }

    @Override
    public void fetchTransactions(String accountIdentifier) {
        mHistoryView.showHistoryFetchingProgress();
        mTransactionsHistory.fetchTransactionsHistory(accountIdentifier);
    }

    @Override
    public void onTransactionsFetchCompleted(List<JournalEntry> transactions) {
        if (transactions == null) {
            showErrorStateView();
        } else {
            int transactionsAmount = transactions.size();
            if (transactionsAmount > 0) {
                mHistoryView.showTransactions(transactions);
            } else {
                showEmptyStateView();
            }
        }
    }

    @Override
    public void onTransactionsFetchError(String message) {
        showEmptyStateView();
    }

    private void showErrorStateView() {
        mHistoryView.showStateView(R.drawable.ic_error_state, R.string.error_oops,
                R.string.error_no_transaction_history_subtitle);
    }

    private void showEmptyStateView() {
        mHistoryView.showStateView(R.drawable.ic_history,
                R.string.empty_no_transaction_history_title,
                R.string.empty_no_transaction_history_subtitle);
    }
}
