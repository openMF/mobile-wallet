package org.mifos.mobilewallet.invoice.domain.usecase;

import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;

import java.util.List;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 11/7/17.
 */

public class FetchLocalInvoices extends UseCase<FetchLocalInvoices.RequestValues,
        FetchLocalInvoices.ResponseValue> {

    private final LocalRepository localRepository;
    private final FineractRepository fineractRepository;

    @Inject
    public FetchLocalInvoices(LocalRepository localRepository,
                              FineractRepository fineractRepository) {
        this.localRepository = localRepository;
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(final RequestValues requestValues) {
        localRepository.getInvoiceList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Invoice>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error fetching local invoices");
                    }

                    @Override
                    public void onNext(List<Invoice> invoices) {
                        getUseCaseCallback().onSuccess(new ResponseValue(invoices));
                    }
                });

    }


    public static final class RequestValues implements UseCase.RequestValues {

        private long accountId;

        public RequestValues(long accountId) {
            this.accountId = accountId;
        }

        public void setAccountId(long accountId) {
            this.accountId = accountId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private List<Invoice> invoices;

        public ResponseValue(List<Invoice> invoices) {
            this.invoices = invoices;
        }

        public List<Invoice> getInvoices() {
            return invoices;
        }
    }

}


