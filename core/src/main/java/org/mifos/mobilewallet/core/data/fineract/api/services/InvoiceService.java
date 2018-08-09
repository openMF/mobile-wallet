package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.Invoice;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ankur on 07/June/2018
 */

public interface InvoiceService {

    @POST(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    Observable<GenericResponse> addInvoice(
            @Path("clientId") String clientId,
            @Body Invoice invoice);

    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}")
    Observable<List<Invoice>> getInvoices(@Path("clientId") String clientId);

    @GET(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    Observable<List<Invoice>> getInvoice(
            @Path("clientId") String clientId,
            @Path("invoiceId") String invoiceId);

    @DELETE(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    Observable<GenericResponse> deleteInvoice(@Path("clientId") String clientId,
            @Path("invoiceId") int invoiceId);

    @PUT(ApiEndPoints.DATATABLES + "/invoices/{clientId}/{invoiceId}")
    Observable<GenericResponse> updateInvoice(
            @Path("clientId") String clientId,
            @Path("invoiceId") long invoiceId,
            @Body Invoice invoice);

}
