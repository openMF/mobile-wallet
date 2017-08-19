package mifos.org.mobilewallet.core.data.fineract.api.services;

import mifos.org.mobilewallet.core.data.fineract.api.ApiEndPoints;
import mifos.org.mobilewallet.core.data.fineract.entity.Page;
import mifos.org.mobilewallet.core.data.fineract.entity.client.Client;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by naman on 19/8/17.
 */

public interface SearchService {

    @GET(ApiEndPoints.SEARCH)
    Observable<Page<Client>> searchClient(@Query("externalId") String query);
}
