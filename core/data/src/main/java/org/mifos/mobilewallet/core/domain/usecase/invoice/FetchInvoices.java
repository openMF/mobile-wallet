package org.mifos.mobilewallet.core.domain.usecase.invoice;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ankur on 11/June/2018
 */

public class FetchInvoices extends
        UseCase<FetchInvoices.RequestValues, FetchInvoices.ResponseValue> {

    private final FineractRepository mFineractRepository;

    @Inject
    public FetchInvoices(FineractRepository fineractRepository) {
        mFineractRepository = fineractRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mFineractRepository.fetchInvoices(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Invoice>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError(e.toString());
                    }

                    @Override
                    public void onNext(List<Invoice> invoices) {
                        getUseCaseCallback().onSuccess(new ResponseValue(invoices));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String clientId;

        public RequestValues(String clientId) {
            this.clientId = clientId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Invoice> mInvoiceList;

        public ResponseValue(
                List<Invoice> invoiceList) {
            mInvoiceList = invoiceList;
        }

        public List<Invoice> getInvoiceList() {
            return mInvoiceList;
        }
    }
}
