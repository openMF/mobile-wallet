package org.mifos.mobilewallet.mifospay.invoice

import android.net.Uri
import org.mifos.mobilewallet.core.data.fineract.entity.Invoice
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 07/June/2018
 */
interface InvoiceContract {
    interface InvoiceView : BaseView<InvoicePresenter?> {
        fun showInvoiceDetails(invoice: Invoice?, merchantId: String?, paymentLink: String?)
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
    }

    interface InvoicePresenter : BasePresenter {
        fun getInvoiceDetails(data: Uri)
    }

    interface InvoicesView : BaseView<InvoicesPresenter?> {
        fun showSnackbar(message: String?)
        fun showInvoices(invoiceList: List<Invoice?>?)
        fun showErrorStateView(drawable: Int, title: Int, subtitle: Int)
        fun showFetchingProcess()
    }

    interface InvoicesPresenter : BasePresenter {
        fun fetchInvoices()
        fun getUniqueInvoiceLink(id: Long): Uri?
    }
}