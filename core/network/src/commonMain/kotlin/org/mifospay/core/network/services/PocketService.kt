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

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.pocket.Pocket
import org.mifospay.core.model.pocket.PocketDelinkPayload
import org.mifospay.core.model.pocket.PocketLinkPayload
import org.mifospay.core.network.utils.ApiEndPoints

interface PocketService {
    @GET(ApiEndPoints.POCKETS)
    fun getPockets(): Flow<Pocket>

    @POST(ApiEndPoints.POCKETS)
    suspend fun linkAccounts(
        @Query("command") command: String = "linkAccounts",
        @Body payload: PocketLinkPayload,
    )

    @POST(ApiEndPoints.POCKETS)
    suspend fun delinkAccounts(
        @Query("command") command: String = "delinkAccounts",
        @Body payload: PocketDelinkPayload,
    )
}
