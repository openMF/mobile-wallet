package org.mifos.mobilewallet.data.local;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.mifos.mobilewallet.invoice.domain.model.Invoice;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by naman on 11/7/17.
 */

@Singleton
public class DatabaseHelper {

    @Inject
    public DatabaseHelper() {

    }

    //only pending invoices are stored in the database, once a invoice has been paid,
    // a new deposit transaction is added to the account and the pending invoice is
    // deleted from database
    public Observable<Invoice> saveInvoice(final Invoice invoice) {
        return Observable.defer(new Func0<Observable<Invoice>>() {
            @Override
            public Observable<Invoice> call() {
                invoice.insert();
                return Observable.just(invoice);
            }
        });
    }

    public Observable<List<Invoice>> getInvoiceList() {
        return Observable.defer(new Func0<Observable<List<Invoice>>>() {
            @Override
            public Observable<List<Invoice>> call() {
                List<Invoice> invoices = SQLite.select()
                        .from(Invoice.class)
                        .queryList();
                return Observable.just(invoices);
            }
        });
    }
}
