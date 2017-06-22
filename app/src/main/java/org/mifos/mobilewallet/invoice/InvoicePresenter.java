package org.mifos.mobilewallet.invoice;

import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.core.UseCaseHandler;
import org.mifos.mobilewallet.home.HomeContract;
import org.mifos.mobilewallet.home.domain.usecase.FetchUserData;
import org.mifos.mobilewallet.invoice.domain.usecase.FetchPaymentMethods;

import javax.inject.Inject;

/**
 * Created by naman on 20/6/17.
 */

public class InvoicePresenter implements InvoiceContract.InvoicePresenter {

    private InvoiceContract.InvoiceView mInvoiceView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    FetchPaymentMethods fetchPaymentMethods;

    @Inject
    public InvoicePresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mInvoiceView = (InvoiceContract.InvoiceView) baseView;
        mInvoiceView.setPresenter(this);
    }

    @Override
    public void fetchPaymentMethods() {
        mUsecaseHandler.execute(fetchPaymentMethods, null,
                new UseCase.UseCaseCallback<FetchPaymentMethods.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchPaymentMethods.ResponseValue response) {
                        mInvoiceView.showPaymentMethods(response.getPaymentMethods());
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
