package org.mifos.mobilewallet.mifospay.network.services

import com.mifos.mobilewallet.model.entity.Invoice
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
import org.mifos.mobilewallet.mifospay.network.GenericResponse
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
    @POST(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    fun addInvoice(
        @Path("clientId") clientId: String,
        @Body invoice: Invoice?
    ): Observable<GenericResponse>

    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    fun getInvoices(@Path("clientId") clientId: String): Observable<List<Invoice>>

    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    fun getInvoice(
        @Path("clientId") clientId: String,
        @Path("invoiceId") invoiceId: String
    ): Observable<List<Invoice>>

    @DELETE(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    fun deleteInvoice(
        @Path("clientId") clientId: String,
        @Path("invoiceId") invoiceId: Int
    ): Observable<GenericResponse>

    @PUT(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    fun updateInvoice(
        @Path("clientId") clientId: String,
        @Path("invoiceId") invoiceId: Long,
        @Body invoice: Invoice?
    ): Observable<GenericResponse>
}
