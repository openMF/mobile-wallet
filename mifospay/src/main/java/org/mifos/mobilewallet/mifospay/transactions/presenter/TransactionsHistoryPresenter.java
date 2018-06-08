package org.mifos.mobilewallet.mifospay.transactions.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.core.domain.usecase.FetchAccountTransactions;
import org.mifos.mobilewallet.core.domain.usecase.FetchAccountTransfer;
import org.mifos.mobilewallet.core.domain.usecase.RunReport;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;

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
    FetchAccountTransfer fetchAccountTransferUseCase;
    @Inject
    RunReport runReportUseCase;
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
                        mTransactionsHistoryView.showTransactions(response.getTransactions());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    @Override
    public void fetchTransfer(final Transaction transaction) {

        if (transaction.getTransferDetail() != null || transaction.getTransferId() == 0) {
            mTransactionsHistoryView.showTransactionDetailDialog(transaction);
            return;
        }

        long transferId = transaction.getTransferId();

        mUsecaseHandler.execute(fetchAccountTransferUseCase,
                new FetchAccountTransfer.RequestValues(transferId),
                new UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchAccountTransfer.ResponseValue response) {

                        transaction.setTransferDetail(response.getTransferDetail());
                        mTransactionsHistoryView.showTransactionDetailDialog(transaction);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

}
