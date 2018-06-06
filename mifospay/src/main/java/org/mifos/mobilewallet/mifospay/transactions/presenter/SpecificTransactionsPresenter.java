package org.mifos.mobilewallet.mifospay.transactions.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.transactions.TransactionsContract;

import javax.inject.Inject;

/**
 * Created by ankur on 08/June/2018
 */

public class SpecificTransactionsPresenter implements
        TransactionsContract.SpecificTransactionsPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private TransactionsContract.SpecificTransactionsView mSpecificTransactionsView;

    @Inject
    public SpecificTransactionsPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mSpecificTransactionsView = (TransactionsContract.SpecificTransactionsView) baseView;
        mSpecificTransactionsView.setPresenter(this);
    }
}
