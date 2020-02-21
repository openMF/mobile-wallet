package org.mifos.mobilewallet.mifospay.history;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.CheckBoxStatus;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TransactionsHistory {

    private final UseCaseHandler mUsecaseHandler;
    public HistoryContract.TransactionsHistoryAsync delegate;
    @Inject
    FetchAccount mFetchAccountUseCase;
    @Inject
    FetchAccountTransactions fetchAccountTransactionsUseCase;
    @Inject
    TaskLooper mTaskLooper;
    @Inject
    UseCaseFactory mUseCaseFactory;
    private List<Transaction> transactions;

    @Inject
    public TransactionsHistory(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
        transactions = new ArrayList<>();
    }

    public void fetchTransactionsHistory(long accountId) {
        fetchTransactionsHistory(accountId, null);
    }

    public void fetchTransactionsHistory(long accountId, List<CheckBoxStatus> filterList) {
        mUsecaseHandler.execute(fetchAccountTransactionsUseCase,
                new FetchAccountTransactions.RequestValues(accountId, filterList),
                new UseCase.UseCaseCallback<FetchAccountTransactions.ResponseValue>() {

                    @Override
                    public void onSuccess(FetchAccountTransactions.ResponseValue response) {
                        transactions = response.getTransactions();
                        delegate.onTransactionsFetchCompleted(transactions);
                    }

                    @Override
                    public void onError(String message) {
                        transactions = null;
                    }
                });
    }
}
