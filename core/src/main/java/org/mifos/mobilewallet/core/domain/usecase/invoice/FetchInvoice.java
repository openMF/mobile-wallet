package org.mifos.mobilewallet.core.domain.usecase.invoice;

import android.net.Uri;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.core.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 07/June/2018
 */

public class FetchInvoice extends UseCase<FetchInvoice.RequestValues, FetchInvoice.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchInvoice(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        Uri paymentLink = requestValues.uniquePaymentLink;
        String scheme = paymentLink.getScheme(); // "https"
        String host = paymentLink.getHost(); // "invoice.mifospay.com"
        try {
            List<String> params = paymentLink.getPathSegments();
            String clientId = params.get(0); // "clientId"
            String invoiceId = params.get(1); // "invoiceId"

            mFineractRepository.fetchInvoice(clientId, invoiceId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<List<Invoice>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            getUseCaseCallback().onError(Constants.INVALID_UPL);
                        }

                        @Override
                        public void onNext(List<Invoice> invoices) {
                            if (invoices.size() > 0) {
                                getUseCaseCallback().onSuccess(new ResponseValue(invoices));
                            } else {
                                getUseCaseCallback().onError(Constants.INVOICE_DOES_NOT_EXIST);
                            }
                        }
                    });
        } catch (IndexOutOfBoundsException e) {
            getUseCaseCallback().onError("Invalid link used to open the App");
        }

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Uri uniquePaymentLink;

        public RequestValues(Uri uniquePaymentLink) {
            this.uniquePaymentLink = uniquePaymentLink;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Invoice> mInvoices;

        public ResponseValue(
                List<Invoice> invoices) {
            mInvoices = invoices;
        }

        public List<Invoice> getInvoices() {
            return mInvoices;
        }
    }
}
