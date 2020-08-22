package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.SearchedEntity
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by naman on 19/8/17.
 */
interface SearchService {

    @GET(ApiEndPoints.SEARCH)
    fun searchResources(
            @Query("query") query: String,
            @Query("resource") resources: String,
            @Query("exactMatch") exactMatch: Boolean): Observable<List<SearchedEntity>>
}