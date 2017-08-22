package mifos.org.mobilewallet.core.data.fineract.api.services;

import java.util.List;

import mifos.org.mobilewallet.core.data.fineract.api.ApiEndPoints;
import mifos.org.mobilewallet.core.data.fineract.entity.SearchedEntity;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by naman on 19/8/17.
 */

public interface SearchService {

    @GET(ApiEndPoints.SEARCH)
    Observable<List<SearchedEntity>> searchResources(@Query("query") String query,
                                                     @Query("resource") String resources,
                                                     @Query("exactMatch") Boolean exactMatch);
}
