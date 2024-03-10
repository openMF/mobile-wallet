package org.mifos.mobilewallet.mifospay.invoice.presenter

import android.net.Uri
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.invoice.FetchInvoice
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.datastore.PreferencesHelper
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract.InvoiceView
import javax.inject.Inject

/**
 * Created by ankur on 07/June/2018
 */
class InvoicePresenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val fetchInvoiceUseCase: FetchInvoice
) : InvoiceContract.InvoicePresenter {
    var mInvoiceView: InvoiceView? = null

    override fun attachView(baseView: BaseView<*>?) {
        mInvoiceView = baseView as InvoiceView?
        mInvoiceView!!.setPresenter(this)
    }

    override fun getInvoiceDetails(data: Uri?) {
        mUseCaseHandler.execute(fetchInvoiceUseCase, FetchInvoice.RequestValues(data),
            object : UseCaseCallback<FetchInvoice.ResponseValue> {
                override fun onSuccess(response: FetchInvoice.ResponseValue) {
                    mInvoiceView?.showInvoiceDetails(
                        response.invoices[0],
                        mPreferencesHelper.fullName + " "
                                + mPreferencesHelper.clientId, data.toString()
                    )
                }

                override fun onError(message: String) {
                    mInvoiceView!!.showToast(message)
                }
            })
    }
}