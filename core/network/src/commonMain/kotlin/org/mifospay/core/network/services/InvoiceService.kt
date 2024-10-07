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

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.entity.Invoice
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.utils.ApiEndPoints

// TODO:: Fix this endpoints, there's no such endpoint for invoices
interface InvoiceService {
    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    suspend fun getInvoices(@Path("clientId") clientId: Int): Flow<List<Invoice>>

    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    suspend fun getInvoice(
        @Path("clientId") clientId: Int,
        @Path("invoiceId") invoiceId: Int,
    ): Flow<Invoice>

    @POST(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    suspend fun addInvoice(
        @Path("clientId") clientId: Int,
        @Body invoice: Invoice?,
    ): Unit

    @PUT(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    suspend fun updateInvoice(
        @Path("clientId") clientId: Int,
        @Path("invoiceId") invoiceId: Int,
        @Body invoice: Invoice?,
    ): Flow<GenericResponse>

    @DELETE(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    suspend fun deleteInvoice(
        @Path("clientId") clientId: Int,
        @Path("invoiceId") invoiceId: Int,
    ): Flow<GenericResponse>
}
