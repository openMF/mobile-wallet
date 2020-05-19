package org.mifos.mobilewallet.mifospay.invoice;

import android.net.Uri;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * Created by ankur on 07/June/2018
 */

public interface InvoiceContract {

    interface InvoiceView extends BaseView<InvoicePresenter> {

        void showInvoiceDetails(Invoice invoice, String merchantId, String paymentLink);

        void showSnackbar(String message);

        void showToast(String message);
    }

    interface InvoicePresenter extends BasePresenter {


        void getInvoiceDetails(Uri data);
    }

    interface InvoicesView extends BaseView<InvoicesPresenter> {

        void showSnackbar(String message);

        void showInvoices(List<Invoice> invoiceList);

        void showErrorStateView(int drawable, int title, int subtitle);

        void showFetchingProcess();
    }

    interface InvoicesPresenter extends BasePresenter {


        void fetchInvoices();

        Uri getUniqueInvoiceLink(long id);
    }
}
