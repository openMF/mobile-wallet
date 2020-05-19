package org.mifos.mobilewallet.mifospay.invoice.presenter;

import android.net.Uri;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.invoice.FetchInvoices;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

/**
 * Created by ankur on 11/June/2018
 */

public class InvoicesPresenter implements InvoiceContract.InvoicesPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final PreferencesHelper mPreferencesHelper;
    @Inject
    FetchInvoices fetchInvoicesUseCase;
    private InvoiceContract.InvoicesView mInvoicesView;

    @Inject
    public InvoicesPresenter(UseCaseHandler useCaseHandler, PreferencesHelper preferencesHelper) {
        mUseCaseHandler = useCaseHandler;
        mPreferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mInvoicesView = (InvoiceContract.InvoicesView) baseView;
        mInvoicesView.setPresenter(this);
    }

    @Override
    public void fetchInvoices() {
        mInvoicesView.showFetchingProcess();
        mUseCaseHandler.execute(fetchInvoicesUseCase,
                new FetchInvoices.RequestValues(mPreferencesHelper.getClientId() + ""),
                new UseCase.UseCaseCallback<FetchInvoices.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchInvoices.ResponseValue response) {
                        mInvoicesView.showInvoices(response.getInvoiceList());
                    }

                    @Override
                    public void onError(String message) {
                        mInvoicesView.showErrorStateView(R.drawable.ic_error_state,
                                R.string.error_oops,
                                R.string.error_no_invoices_found);
                    }
                });
    }

    @Override
    public Uri getUniqueInvoiceLink(long id) {
        Uri data = Uri.parse(
                Constants.INVOICE_DOMAIN + mPreferencesHelper.getClientId() + "/" + id);
        return data;
    }
}
