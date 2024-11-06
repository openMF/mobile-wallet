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

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import org.mifospay.core.network.model.entity.SearchedEntity
import org.mifospay.core.network.utils.ApiEndPoints

interface SearchService {
    @GET(ApiEndPoints.SEARCH)
    suspend fun searchResources(
        @Query("query") query: String,
        @Query("resource") resources: String,
        @Query("exactMatch") exactMatch: Boolean,
    ): List<SearchedEntity>
}
