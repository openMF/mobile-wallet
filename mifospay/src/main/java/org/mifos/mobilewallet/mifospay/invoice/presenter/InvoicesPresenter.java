package org.mifos.mobilewallet.mifospay.invoice.presenter;

import android.net.Uri;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.invoice.FetchInvoices;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;

import javax.inject.Inject;

/**
 * This class contains components of the Presenter required by Invoices.
 * @author ankur
 * @since 11/June/2018
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

    /**
     * This function attaches a view.
     * @param baseView This is the view to be attached.
     */
    @Override
    public void attachView(BaseView baseView) {
        mInvoicesView = (InvoiceContract.InvoicesView) baseView;
        mInvoicesView.setPresenter(this);
    }

    /**
     * This function fetches the Invoices.
     */
    @Override
    public void fetchInvoices() {
        mUseCaseHandler.execute(fetchInvoicesUseCase,
                new FetchInvoices.RequestValues(mPreferencesHelper.getClientId() + ""),
                new UseCase.UseCaseCallback<FetchInvoices.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchInvoices.ResponseValue response) {
                        DebugUtil.log("ivoices fetched successfully",
                                response.getInvoiceList().size());
                        mInvoicesView.showInvoices(response.getInvoiceList());
                    }

                    @Override
                    public void onError(String message) {
                        DebugUtil.log("unable to fetvh invoices");
                        mInvoicesView.hideProgress();
                        mInvoicesView.showToast(Constants.ERROR_FETCHING_INVOICES);
                    }
                });
    }

    /**
     * This function gets the Unique Invoice Link
     * @param id This is the client id of the type long.
     * @return Returns data of the type Uri.
     */
    @Override
    public Uri getUniqueInvoiceLink(long id) {
        Uri data = Uri.parse(
                Constants.INVOICE_DOMAIN + mPreferencesHelper.getClientId() + "/" + id);
        return data;
    }
}
