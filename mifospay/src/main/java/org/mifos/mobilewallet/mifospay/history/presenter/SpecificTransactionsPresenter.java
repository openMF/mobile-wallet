package org.mifos.mobilewallet.mifospay.history.presenter;

import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.history.HistoryContract;

import javax.inject.Inject;

/**
 * Created by ankur on 08/June/2018
 */

public class SpecificTransactionsPresenter implements
        HistoryContract.SpecificTransactionsPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private HistoryContract.SpecificTransactionsView mSpecificTransactionsView;

    @Inject
    public SpecificTransactionsPresenter(UseCaseHandler useCaseHandler) {
        mUseCaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mSpecificTransactionsView = (HistoryContract.SpecificTransactionsView) baseView;
        mSpecificTransactionsView.setPresenter(this);
    }
}
