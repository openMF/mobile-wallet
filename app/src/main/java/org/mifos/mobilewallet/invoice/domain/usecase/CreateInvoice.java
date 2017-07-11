package org.mifos.mobilewallet.invoice.domain.usecase;

import org.mifos.mobilewallet.core.UseCase;
import org.mifos.mobilewallet.data.local.DatabaseHelper;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 17/6/17.
 */

public class CreateInvoice extends UseCase<CreateInvoice.RequestValues,
        CreateInvoice.ResponseValue> {

    private final DatabaseHelper databaseHelper;

    @Inject
    public CreateInvoice(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    @Override
    protected void executeUseCase(CreateInvoice.RequestValues requestValues) {
        databaseHelper.saveInvoice(requestValues.getInvoice())
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


