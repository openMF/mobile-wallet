package org.mifos.mobilewallet.mifospay.history.presenter;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Account;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.model.TransactionType;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.history.TransactionsHistory;

import java.util.ArrayList;

import java.util.List;

import javax.inject.Inject;


public class HistoryPresenter implements
        HistoryContract.TransactionsHistoryPresenter,
        HistoryContract.TransactionsHistoryAsync {

    private final UseCaseHandler mUseCaseHandler;
    private final LocalRepository mLocalRepository;
    @Inject
    FetchAccount mFetchAccountUseCase;
    @Inject
    FetchAccountTransactions fetchAccountTransactionsUseCase;
    @Inject
    TaskLooper mTaskLooper;
    @Inject
    UseCaseFactory mUseCaseFactory;
    @Inject
    TransactionsHistory mTransactionsHistory;
    private HistoryContract.HistoryView mHistoryView;
    private Account mAccount;
    private List<Transaction> allTransactions;


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
    public void fetchTransactions() {
        mHistoryView.showHistoryFetchingProgress();
        mUseCaseHandler.execute(mFetchAccountUseCase,
                new FetchAccount.RequestValues(mLocalRepository.getClientDetails().getClientId()),
                new UseCase.UseCaseCallback<FetchAccount.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccount.ResponseValue response) {
                        mAccount = response.getAccount();
                        mTransactionsHistory
                                .fetchTransactionsHistory(response.getAccount().getId());
                    }

                    @Override
                    public void onError(String message) {
                        showErrorStateView();
                    }
                });
    }

    @Override
    public void filterTransactionType(TransactionType type) {
        if (type != null) {
            List<Transaction> filterTransactions = new ArrayList<>();
            for (Transaction transaction : allTransactions) {
                if (transaction.getTransactionType() == type) {
                    filterTransactions.add(transaction);
                }
            }
            mHistoryView.refreshTransactions(filterTransactions);
        } else {
            mHistoryView.refreshTransactions(allTransactions);
        }
    }

    @Override
    public void onTransactionsFetchCompleted(List<Transaction> transactions) {
        if (transactions == null) {
            showErrorStateView();
        } else {
            int transactionsAmount = transactions.size();
            if (transactionsAmount > 0) {
                this.allTransactions = transactions;
                mHistoryView.showTransactions(transactions);
            } else {
                showEmptyStateView();
            }
        }
    }

    @Override
    public void handleTransactionClick(int transactionIndex) {
        String accountNumber = mAccount != null ? mAccount.getNumber() : "";
        mHistoryView.showTransactionDetailDialog(transactionIndex, accountNumber);
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
