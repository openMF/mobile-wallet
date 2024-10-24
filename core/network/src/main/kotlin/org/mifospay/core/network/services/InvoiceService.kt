/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.services

import com.mifospay.core.model.entity.invoice.Invoice
import com.mifospay.core.model.entity.invoice.InvoiceEntity
import org.mifospay.core.network.ApiEndPoints
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

/**
 * Created by ankur on 07/June/2018
 */
interface InvoiceService {
    @POST(ApiEndPoints.DATATABLES + "/invoice/{clientId}")
    fun addInvoice(
        @Path("clientId") clientId: Long,
        @Body invoice: InvoiceEntity?,
    )

    @GET(ApiEndPoints.DATATABLES + "/invoice/{clientId}")
    fun getInvoices(@Path("clientId") clientId: Long): Observable<List<Invoice>>

    @GET(ApiEndPoints.DATATABLES + "/invoice/{clientId}/{invoiceId}")
    fun getInvoice(
        @Path("clientId") clientId: Long,
        @Path("invoiceId") invoiceId: Long,
    ): Observable<List<Invoice>>

    @DELETE(ApiEndPoints.DATATABLES + "/invoice/{clientId}/{invoiceId}")
    fun deleteInvoice(
        @Path("clientId") clientId: Long,
        @Path("invoiceId") invoiceId: Long,
    )

    @PUT(ApiEndPoints.DATATABLES + "/invoice/{clientId}/{invoiceId}")
    fun updateInvoice(
        @Path("clientId") clientId: Long,
        @Path("invoiceId") invoiceId: Long,
        @Body invoice: Invoice?,
    )
}
