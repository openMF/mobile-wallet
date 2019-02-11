package org.mifos.mobilewallet.mifospay.invoice;

import android.net.Uri;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * This contract class acts as interface between the UI and the Presenter components of Invoice.
 * @author ankur
 * @since 07/June/2018
 */

public interface InvoiceContract {

    /**
     * Contains all the functions of the Invoice UI component.
     */
    interface InvoiceView extends BaseView<InvoicePresenter> {

        void showInvoiceDetails(Invoice invoice, String merchantId, String paymentLink);

        void showSnackbar(String message);

        void showToast(String message);
    }

    /**
     * Contains all the functions of the Presenter component.
     */
    interface InvoicePresenter extends BasePresenter {


        void getInvoiceDetails(Uri data);
    }

    /**
     * Contains all the functions of the Invoices UI component.
     */
    interface InvoicesView extends BaseView<InvoicesPresenter> {

        void showSnackbar(String message);

        void showToast(String message);

        void showInvoices(List<Invoice> invoiceList);

        void hideProgress();
    }

    /**
     * Contains all the functions of the Invoices Presenter component.
     */
    interface InvoicesPresenter extends BasePresenter {


        void fetchInvoices();

        Uri getUniqueInvoiceLink(long id);
    }
}
