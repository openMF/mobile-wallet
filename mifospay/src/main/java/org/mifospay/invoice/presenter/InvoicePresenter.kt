package org.mifospay.invoice.presenter

import android.net.Uri
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.invoice.FetchInvoice
import org.mifospay.base.BaseView
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.invoice.InvoiceContract
import org.mifospay.invoice.InvoiceContract.InvoiceView
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