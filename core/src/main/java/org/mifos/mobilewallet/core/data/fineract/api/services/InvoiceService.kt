package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.Invoice
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import retrofit2.http.*
import rx.Observable

/**
 * Created by ankur on 07/June/2018
 */
interface InvoiceService {
    @POST(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    fun addInvoice(
        @Path("clientId") clientId: String?,
        @Body invoice: Invoice?
    ): Observable<GenericResponse?>?

    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    fun getInvoices(@Path("clientId") clientId: String?): Observable<List<Invoice?>?>?

    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    fun getInvoice(
        @Path("clientId") clientId: String?,
        @Path("invoiceId") invoiceId: String?
    ): Observable<List<Invoice?>?>?

    @DELETE(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    fun deleteInvoice(
        @Path("clientId") clientId: String?,
        @Path("invoiceId") invoiceId: Int
    ): Observable<GenericResponse?>?

    @PUT(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    fun updateInvoice(
        @Path("clientId") clientId: String?,
        @Path("invoiceId") invoiceId: Long,
        @Body invoice: Invoice?
    ): Observable<GenericResponse?>?
}