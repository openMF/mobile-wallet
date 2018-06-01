package org.mifos.mobilewallet.invoice.domain.usecase;

import org.mifos.mobilewallet.data.local.LocalRepository;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 17/6/17.
 */

public class CreateInvoice extends UseCase<CreateInvoice.RequestValues,
        CreateInvoice.ResponseValue> {

    private final LocalRepository localRepository;

    @Inject
    public CreateInvoice(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }


    @Override
    protected void executeUseCase(RequestValues requestValues) {
        localRepository.saveInvoice(requestValues.getInvoice())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Invoice>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUseCaseCallback().onError("Error saving invoice");
                    }

                    @Override
                    public void onNext(Invoice invoice) {
                        getUseCaseCallback().onSuccess(new ResponseValue(invoice));
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private Invoice invoice;

        public RequestValues(Invoice invoice) {
            this.invoice = invoice;
        }

        public Invoice getInvoice() {
            return invoice;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private Invoice invoice;

        public ResponseValue(Invoice invoice) {
            this.invoice = invoice;
        }

        public Invoice getInvoice() {
            return invoice;
        }
    }

}


