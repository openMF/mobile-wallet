package org.mifos.mobilewallet.invoice.presenter;

import org.mifos.mobilewallet.account.domain.usecase.FetchAccounts;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.base.UseCase;
import org.mifos.mobilewallet.base.UseCaseHandler;
import org.mifos.mobilewallet.invoice.InvoiceContract;
import org.mifos.mobilewallet.invoice.domain.usecase.FetchRecentInvoices;

import javax.inject.Inject;

/**
 * Created by naman on 11/7/17.
 */

public class RecentInvoicePresenter implements InvoiceContract.RecentInvoicePresenter {

    private InvoiceContract.RecentInvoiceView mInvoiceView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchRecentInvoices fetchRecentInvoices;

    @Inject
    FetchAccounts fetchAccounts;

    @Inject
    public RecentInvoicePresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mInvoiceView = (InvoiceContract.RecentInvoiceView) baseView;
        mInvoiceView.setPresenter(this);
    }

    @Override
    public void fetchRecentInvoices(long accountId) {
        mUsecaseHandler.execute(fetchRecentInvoices,
                new FetchRecentInvoices.RequestValues(accountId),
                new UseCase.UseCaseCallback<FetchRecentInvoices.ResponseValue>() {
            @Override
            public void onSuccess(FetchRecentInvoices.ResponseValue response) {
                mInvoiceView.showInvoices(response.getInvoices());
            }

            @Override
            public void onError(String message) {
                mInvoiceView.showError(message);
            }
        });
    }

    @Override
    public void fetchAccounts() {
        mUsecaseHandler.execute(fetchAccounts, null,
                new UseCase.UseCaseCallback<FetchAccounts.ResponseValue>() {
            @Override
            public void onSuccess(FetchAccounts.ResponseValue response) {
                mInvoiceView.showAccounts(response.getAccountList());
            }

            @Override
            public void onError(String message) {
                mInvoiceView.showError(message);
            }
        });
    }
}

