package org.mifos.mobilewallet.core.data.fineract.api.services;


import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.Page;
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.core.domain.model.NewAccount;
import org.mifos.mobilewallet.core.domain.model.client.NewClient;
import org.mifos.mobilewallet.core.domain.usecase.client.CreateClient;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ClientService {

    @GET(ApiEndPoints.CLIENTS)
    Observable<Page<Client>> getClients();

    @GET(ApiEndPoints.CLIENTS + "/{clientId}")
    Observable<Client> getClientForId(@Path("clientId") long clientId);

    @PUT(ApiEndPoints.CLIENTS + "/{clientId}")
    Observable<ResponseBody> updateClient(@Path("clientId") long clientId,
            @Body Object payload);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/images")
    Observable<ResponseBody> getClientImage(@Path("clientId") long clientId);

    @PUT(ApiEndPoints.CLIENTS + "/{clientId}/images")
    Observable<GenericResponse> updateClientImage(
            @Path("clientId") long clientId,
            @Part() MultipartBody.Part typedFile);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getClientAccounts(@Path("clientId") long clientId);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getAccounts(@Path("clientId") long clientId,
            @Query("fields") String accountType);

    @POST(ApiEndPoints.CLIENTS)
    Observable<CreateClient.ResponseValue> createClient(@Body NewClient newClient);

    @POST
    Observable<GenericResponse> createAccount(@Body NewAccount newAccount);
}
