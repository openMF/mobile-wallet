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

import android.util.Log
import com.mifospay.core.model.entity.invoice.Invoice
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchInvoices(
    private val mFineractRepository: FineractRepository,
) :
    UseCase<FetchInvoices.RequestValues, FetchInvoices.ResponseValue>() {

    class RequestValues(val clientId: String) : UseCase.RequestValues
    class ResponseValue(
        val invoiceList: List<Invoice?>,
    ) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.fetchInvoices(requestValues.clientId.toLong())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<Invoice>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        Log.e("Invoices", e.message.toString())
                        useCaseCallback.onError(e.toString())
                    }

                    override fun onNext(invoices: List<Invoice>) {
                        Log.d("invoice@@@", invoices.toString())
                        useCaseCallback.onSuccess(ResponseValue(invoices))
                    }
                },
            )
    }
}
