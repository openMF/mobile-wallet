package org.mifospay.core.network.services

import com.mifospay.core.model.entity.SearchedEntity
import org.mifospay.core.network.ApiEndPoints
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
