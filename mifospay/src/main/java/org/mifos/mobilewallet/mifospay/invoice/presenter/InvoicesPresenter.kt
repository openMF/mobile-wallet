package org.mifos.mobilewallet.mifospay.invoice.presenter

import android.net.Uri
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.invoice.FetchInvoices
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.core.datastore.PreferencesHelper
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract.InvoicesView
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * Created by ankur on 11/June/2018
 */
class InvoicesPresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val fetchInvoicesUseCase: FetchInvoices
) : InvoiceContract.InvoicesPresenter {

    private var mInvoicesView: InvoicesView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mInvoicesView = baseView as InvoicesView?
        mInvoicesView!!.setPresenter(this)
    }

    override fun fetchInvoices() {
        mInvoicesView!!.showFetchingProcess()
        mUseCaseHandler.execute(fetchInvoicesUseCase,
            FetchInvoices.RequestValues(mPreferencesHelper.clientId.toString() + ""),
            object : UseCaseCallback<FetchInvoices.ResponseValue> {
                override fun onSuccess(response: FetchInvoices.ResponseValue) {
                    mInvoicesView!!.showInvoices(response?.invoiceList)
                }

                override fun onError(message: String) {
                    mInvoicesView!!.showErrorStateView(
                        R.drawable.ic_error_state,
                        R.string.error_oops,
                        R.string.error_no_invoices_found
                    )
                }
            })
    }

    override fun getUniqueInvoiceLink(id: Long): Uri? {
        return Uri.parse(
            Constants.INVOICE_DOMAIN + mPreferencesHelper.clientId + "/" + id
        )
    }
}