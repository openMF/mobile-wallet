package org.mifos.mobilewallet.mifospay.network.services

import com.mifos.mobilewallet.model.entity.SearchedEntity
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface SearchService {
    @GET(ApiEndPoints.SEARCH)
    fun searchResources(
        @Query("query") query: String,
        @Query("resource") resources: String,
        @Query("exactMatch") exactMatch: Boolean
    ): Observable<List<SearchedEntity>>
}
