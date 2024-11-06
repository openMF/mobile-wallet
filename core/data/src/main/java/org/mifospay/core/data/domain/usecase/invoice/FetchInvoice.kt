/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.invoice

import android.net.Uri
import android.util.Log
import com.mifospay.core.model.entity.invoice.Invoice
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchInvoice(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchInvoice.RequestValues, FetchInvoice.ResponseValue>() {

    class RequestValues(val uniquePaymentLink: Uri?) : UseCase.RequestValues
    class ResponseValue(
        val invoices: List<Invoice?>,
    ) : UseCase.ResponseValue
    override fun executeUseCase(requestValues: RequestValues) {
        val paymentLink = requestValues.uniquePaymentLink
        try {
            val params = paymentLink?.pathSegments
            val clientId = params?.get(0)?.toLong() // "clientId"
            val invoiceId = params?.get(1)?.toLong() // "invoiceId
            if (clientId != null && invoiceId != null) {
                mFineractRepository.fetchInvoice(clientId, invoiceId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        object : Subscriber<List<Invoice?>?>() {
                            override fun onCompleted() {}
                            override fun onError(e: Throwable) {
                                useCaseCallback.onError(Constants.INVALID_UPL)
                            }

                            override fun onNext(invoices: List<Invoice?>?) {
                                if (invoices?.isNotEmpty() == true) {
                                    useCaseCallback.onSuccess(ResponseValue(invoices))
                                } else {
                                    useCaseCallback.onError(Constants.INVOICE_DOES_NOT_EXIST)
                                }
                            }
                        },
                    )
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("Error", e.message.toString())
            useCaseCallback.onError("Invalid link used to open the App")
        }
    }
}
