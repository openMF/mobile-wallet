package org.mifos.mobilewallet.mifospay.history;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.mifos.mobilewallet.core.utils.Constants.FETCH_ACCOUNT_TRANSFER_USECASE;

public class TransactionsHistory {

    public HistoryContract.TransactionsHistoryAsync delegate;

    private List<Transaction> transactions;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchAccount mFetchAccountUseCase;

    @Inject
    FetchAccountTransactions fetchAccountTransactionsUseCase;

    @Inject
    TaskLooper mTaskLooper;

    @Inject
    UseCaseFactory mUseCaseFactory;

    @Inject
    public TransactionsHistory(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
        transactions = new ArrayList<>();
    }

    public void fetchTransactionsHistory(long accountId) {
        mUsecaseHandler.execute(fetchAccountTransactionsUseCase,
                new FetchAccountTransactions.RequestValues(accountId),
                new UseCase.UseCaseCallback<FetchAccountTransactions.ResponseValue>() {

                    @Override
                    public void onSuccess(FetchAccountTransactions.ResponseValue response) {

                        transactions = response.getTransactions();
                        delegate.onTransactionsFetchCompleted(transactions);

                        if (transactions != null && transactions.size() > 0) {

                            for (int i = 0; i < transactions.size(); i++) {

                                final Transaction transaction = transactions.get(i);

                                if (transaction.getTransferDetail() == null
                                        && transaction.getTransferId() != 0) {

                                    long transferId = transaction.getTransferId();

                                    mTaskLooper.addTask(
                                            mUseCaseFactory
                                                    .getUseCase(FETCH_ACCOUNT_TRANSFER_USECASE),
                                            new FetchAccountTransfer.RequestValues(transferId),
                                            new TaskLooper.TaskData(Constants.TRANSFER_DETAILS, i));
                                }
                            }

                            mTaskLooper.listen(new TaskLooper.Listener() {
                                @Override
                                public <R extends UseCase.ResponseValue> void onTaskSuccess(
                                        TaskLooper.TaskData taskData, R response) {

                                    switch (taskData.getTaskName()) {
                                        case Constants.TRANSFER_DETAILS:
                                            FetchAccountTransfer.ResponseValue responseValue =
                                                    (FetchAccountTransfer.ResponseValue) response;
                                            int index = taskData.getTaskId();
                                            transactions.get(index).setTransferDetail(
                                                    responseValue.getTransferDetail());
                                    }
                                }

                                @Override
                                public void onComplete() {
                                    delegate.onTransactionsFetchCompleted(transactions);
                                }

                                @Override
                                public void onFailure(String message) {
                                    transactions = null;
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String message) {
                        transactions = null;
                    }
                });
    }
}
