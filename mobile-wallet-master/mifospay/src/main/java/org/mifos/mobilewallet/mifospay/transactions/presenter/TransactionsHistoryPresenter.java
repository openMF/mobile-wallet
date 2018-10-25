package org.mifos.mobilewallet.mifospay.transactions.presenter;

import static org.mifos.mobilewallet.core.utils.Constants.FETCH_ACCOUNT_TRANSFER_USECASE;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchTransactionReceipt;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by naman on 17/8/17.
 */

public class TransactionsHistoryPresenter implements
        TransactionsContract.TransactionsHistoryPresenter {

    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchAccountTransactions fetchAccountTransactionsUseCase;

    @Inject
    FetchTransactionReceipt mFetchTransactionReceiptUseCase;

    @Inject
    TaskLooper mTaskLooper;

    @Inject
    UseCaseFactory mUseCaseFactory;

    private TransactionsContract.TransactionsHistoryView mTransactionsHistoryView;

    @Inject
    public TransactionsHistoryPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransactionsHistoryView = (TransactionsContract.TransactionsHistoryView) baseView;
        mTransactionsHistoryView.setPresenter(this);
    }

    @Override
    public void fetchTransactions(long accountId) {

        mUsecaseHandler.execute(fetchAccountTransactionsUseCase,
                new FetchAccountTransactions.RequestValues(accountId),
                new UseCase.UseCaseCallback<FetchAccountTransactions.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccountTransactions.ResponseValue response) {

                        final List<Transaction> transactions = response.getTransactions();
                        if (transactions == null || transactions.size() == 0) {
                            mTransactionsHistoryView.showTransactions(transactions);
                            return;
                        }

                        for (int i = 0; i < transactions.size(); i++) {

                            final Transaction transaction = transactions.get(i);

                            if (transaction.getTransferDetail() == null
                                    && transaction.getTransferId() != 0) {

                                long transferId = transaction.getTransferId();

                                mTaskLooper.addTask(
                                        mUseCaseFactory.getUseCase(FETCH_ACCOUNT_TRANSFER_USECASE),
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
                                mTransactionsHistoryView.showTransactions(transactions);
                            }

                            @Override
                            public void onFailure(String message) {
                                mTransactionsHistoryView.hideSwipeProgress();
                                mTransactionsHistoryView.showToast(
                                        Constants.ERROR_FETCHING_TRANSACTIONS);
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        mTransactionsHistoryView.hideSwipeProgress();
                        mTransactionsHistoryView.showToast(Constants.ERROR_FETCHING_TRANSACTIONS);
                    }
                });
    }
}
