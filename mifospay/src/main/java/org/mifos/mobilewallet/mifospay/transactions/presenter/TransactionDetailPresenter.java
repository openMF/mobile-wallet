package org.mifos.mobilewallet.mifospay.transactions.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.RunReport;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;

import javax.inject.Inject;

/**
 * Created by ankur on 06/June/2018
 */

public class TransactionDetailPresenter implements TransactionsContract.TransactionDetailPresenter {

    private final UseCaseHandler mUseCaseHandler;
    @Inject
    RunReport runReportUseCase;
    private TransactionsContract.TransactionDetailView mTransactionDetailView;

    @Inject
    public TransactionDetailPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mTransactionDetailView = (TransactionsContract.TransactionDetailView) baseView;
        mTransactionDetailView.setPresenter(this);
    }
}
