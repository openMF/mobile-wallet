package org.mifos.mobilewallet.invoice.domain.usecase;

import org.mifos.mobilewallet.account.domain.model.Account;
import org.mifos.mobilewallet.account.domain.usecase.FetchAccounts;
import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.fineract.repository.FineractRepository;
import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;
import org.mifos.mobilewallet.invoice.domain.model.PaymentMethod;

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
    protected void executeUseCase(FetchRecentInvoices.RequestValues requestValues) {
        fineractRepository.getAccountTransactions(requestValues.accountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Invoice>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error fetching account transactions");
                    }

                    @Override
                    public void onNext(List<Invoice> invoices) {
                        if (invoices != null && invoices.size() != 0) {
                            getUseCaseCallback().onSuccess(new FetchRecentInvoices.ResponseValue(invoices));
                        } else {
                            getUseCaseCallback().onError("No recent transactions found");
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


