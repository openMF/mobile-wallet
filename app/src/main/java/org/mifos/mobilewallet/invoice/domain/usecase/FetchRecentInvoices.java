package org.mifos.mobilewallet.invoice.domain.usecase;

import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 11/7/17.
 */

public class FetchRecentInvoices extends UseCase<FetchRecentInvoices.RequestValues,
        FetchRecentInvoices.ResponseValue> {

    private final LocalRepository localRepository;
    private final FineractRepository fineractRepository;

    @Inject
    public FetchRecentInvoices(LocalRepository localRepository, FineractRepository fineractRepository) {
        this.localRepository = localRepository;
        this.fineractRepository = fineractRepository;
    }


    @Override
    protected void executeUseCase(final FetchRecentInvoices.RequestValues requestValues) {
        localRepository.getInvoiceList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Invoice>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        fetchRemoteTransactions(new ArrayList<Invoice>(), requestValues);
                        getUseCaseCallback().onError("Error fetching account transactions");
                    }

                    @Override
                    public void onNext(List<Invoice> invoices) {
                        if (invoices != null && invoices.size() != 0) {
                            fetchRemoteTransactions(invoices, requestValues);
                        } else {
                            invoices = new ArrayList<>();
                            fetchRemoteTransactions(invoices, requestValues);
                        }
                    }
                });

    }

    private void fetchRemoteTransactions(final List<Invoice> localList, RequestValues requestValues) {
        fineractRepository.getAccountTransactions(requestValues.accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Invoice>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onSuccess(new ResponseValue(localList));
                        getUseCaseCallback().onError("Error fetching remote account transactions");
                    }

                    @Override
                    public void onNext(List<Invoice> invoices) {
                        if (invoices != null && invoices.size() != 0) {
                            for (Invoice invoice : invoices) {
                                for (int i=0; i < localList.size(); i++) {
                                    if (invoice.getInvoiceId().equals(localList.get(i).getInvoiceId())) {
                                        //transaction exists on remote for this invoiceid
                                        //which means that invoice has been paid
                                        //remove this invoice from local database
                                        localRepository.removeInvoice(localList.get(i));
                                        localList.remove(i);
                                    }

                                }
                            }

                            localList.addAll(invoices);
                            getUseCaseCallback().onSuccess(new ResponseValue(localList));
                        } else {
                           getUseCaseCallback().onSuccess(new ResponseValue(localList));
                        }
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


