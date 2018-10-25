package org.mifos.mobilewallet.mifospay.invoice.presenter;

import android.net.Uri;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.invoice.FetchInvoice;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract;

import javax.inject.Inject;

/**
 * Created by ankur on 07/June/2018
 */

public class InvoicePresenter implements InvoiceContract.InvoicePresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final PreferencesHelper mPreferencesHelper;
    InvoiceContract.InvoiceView mInvoiceView;
    @Inject
    FetchInvoice fetchInvoiceUseCase;

    @Inject
    public InvoicePresenter(UseCaseHandler useCaseHandler, PreferencesHelper preferencesHelper) {
        mUseCaseHandler = useCaseHandler;
        mPreferencesHelper = preferencesHelper;
    }


    @Override
    public void attachView(BaseView baseView) {
        mInvoiceView = (InvoiceContract.InvoiceView) baseView;
        mInvoiceView.setPresenter(this);
    }

    @Override
    public void getInvoiceDetails(final Uri data) {

        mUseCaseHandler.execute(fetchInvoiceUseCase, new FetchInvoice.RequestValues(data),
                new UseCase.UseCaseCallback<FetchInvoice.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchInvoice.ResponseValue response) {
                        mInvoiceView.showInvoiceDetails(response.getInvoices().get(0),
                                mPreferencesHelper.getFullName() + " "
                                        + mPreferencesHelper.getClientId(), data.toString());
                    }

                    @Override
                    public void onError(String message) {
                        mInvoiceView.showToast(message);
                    }
                });
    }
}
