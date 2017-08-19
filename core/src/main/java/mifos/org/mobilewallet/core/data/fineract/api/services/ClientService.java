package mifos.org.mobilewallet.core.data.fineract.api.services;


import mifos.org.mobilewallet.core.data.fineract.api.ApiEndPoints;
import mifos.org.mobilewallet.core.data.fineract.entity.Page;
import mifos.org.mobilewallet.core.data.fineract.entity.client.Client;
import mifos.org.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ClientService {

    String CLIENT_ID = "clientId";

    @GET(ApiEndPoints.CLIENTS)
    Observable<Page<Client>> getClients();

    @GET(ApiEndPoints.CLIENTS)
    Observable<Page<Client>> searchClient(@Query("externalId") String query);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}")
    Observable<Client> getClientForId(@Path(CLIENT_ID) long clientId);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/images")
    Observable<ResponseBody> getClientImage(@Path(CLIENT_ID) long clientId);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getClientAccounts(@Path(CLIENT_ID) long clientId);

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    Observable<ClientAccounts> getAccounts(@Path(CLIENT_ID) long clientId,
                                           @Query("fields") String accountType);

}
