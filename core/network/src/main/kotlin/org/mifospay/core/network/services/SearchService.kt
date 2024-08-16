/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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
        @Query("exactMatch") exactMatch: Boolean,
    ): Observable<List<SearchedEntity>>
}
