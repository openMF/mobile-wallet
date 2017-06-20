package org.mifos.mobilewallet.invoice;

import org.mifos.mobilewallet.core.BasePresenter;
import org.mifos.mobilewallet.core.BaseView;
import org.mifos.mobilewallet.invoice.domain.model.PaymentMethod;

import java.util.List;

/**
 * Created by naman on 20/6/17.
 */

public interface InvoiceContract {

    interface InvoiceView extends BaseView<InvoicePresenter> {

        void showPaymentMethods(List<PaymentMethod> methods);
    }

    interface InvoicePresenter extends BasePresenter {

        void fetchPaymentMethods();
    }
}
