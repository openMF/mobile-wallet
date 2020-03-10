package org.mifos.mobilewallet.mifospay.history.presenter;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransfer;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import static org.mifos.mobilewallet.core.utils.Constants.FETCH_ACCOUNT_TRANSFER_USECASE;

/**
 * Created by ankur on 08/June/2018
 */

public class SpecificTransactionsPresenter implements
        HistoryContract.SpecificTransactionsPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private HistoryContract.SpecificTransactionsView mSpecificTransactionsView;

    @Inject
    TaskLooper mTaskLooper;

    @Inject
    UseCaseFactory mUseCaseFactory;

    @Inject
    public SpecificTransactionsPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mSpecificTransactionsView = (HistoryContract.SpecificTransactionsView) baseView;
        mSpecificTransactionsView.setPresenter(this);
    }

    @Override
    public ArrayList<Transaction> getSpecificTransactions(final ArrayList<Transaction> transactions,
                                                          final String secondAccountNumber) {
        final ArrayList<Transaction> specificTransactions = new ArrayList<>();
        mSpecificTransactionsView.showProgress();
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
                    for (Transaction transaction : transactions) {
                        if (transaction.getTransferDetail() != null
                                && (transaction.getTransferDetail().getFromAccount()
                                .getAccountNo().equals(
                                secondAccountNumber)
                                || transaction.getTransferDetail().getToAccount()
                                .getAccountNo().equals(
                                secondAccountNumber))) {

                            specificTransactions.add(transaction);
                        }
                    }

                    mSpecificTransactionsView.showSpecificTransactions(specificTransactions);
                }

                @Override
                public void onFailure(String message) {
                    mSpecificTransactionsView.showStateView(
                            R.drawable.ic_error_state,
                            R.string.error_oops,
                            R.string.error_specific_transactions);
                }
            });
        }

        return specificTransactions;
    }
}
