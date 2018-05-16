package org.mifos.mobilewallet.core.data.fineract.api.services;


import android.net.Uri;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.entity.Page;
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.core.data.fineract.entity.payload.UpdateVpaPayload;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ClientService {

    String CLIENT_ID = "clientId";

    @GET(ApiEndPoints.CLIENTS)
    Observable<Page<Client>> getClients();

    @GET(ApiEndPoints.CLIENTS + "/{clientId}")
    Observable<Client> getClientForId(@Path(CLIENT_ID) long clientId);

    @PUT(ApiEndPoints.CLIENTS + "/{clientId}")
    Observable<ResponseBody> updateClientVpa(@Path(CLIENT_ID) long clientId,
            @Body UpdateVpaPayload payload);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/images")
    Observable<ResponseBody> getClientImage(@Path(CLIENT_ID) long clientId);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getClientAccounts(@Path(CLIENT_ID) long clientId);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getAccounts(@Path(CLIENT_ID) long clientId,
            @Query("fields") String accountType);

    @POST(ApiEndPoints.CLIENTS + "/{clientId}/images")
    Observable<ResponseBody> uploadKYCDocs(@Path(CLIENT_ID) long clientId,
            @Body Uri uri);

}
