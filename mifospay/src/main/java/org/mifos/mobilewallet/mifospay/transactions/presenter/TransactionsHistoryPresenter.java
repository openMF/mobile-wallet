package org.mifos.mobilewallet.mifospay.transactions.presenter;

import org.mifos.mobilewallet.core.base.TaskLooper;
import org.mifos.mobilewallet.core.base.UseCaseFactory;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccountTransactions;
import org.mifos.mobilewallet.core.domain.usecase.account.FetchTransactionReceipt;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsHistory;

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

    @Inject
    TransactionsHistory transactionsHistory;

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
    }
}
