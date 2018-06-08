package org.mifos.mobilewallet.mifospay.invoice;

import android.net.Uri;

import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

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
}
