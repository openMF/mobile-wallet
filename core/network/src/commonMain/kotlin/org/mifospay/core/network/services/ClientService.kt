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
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.domain.client.NewClient
import org.mifospay.core.model.entity.Page
import org.mifospay.core.model.entity.client.ClientAccounts
import org.mifospay.core.model.entity.client.ClientEntity
import org.mifospay.core.network.model.ClientResponse
import org.mifospay.core.network.utils.ApiEndPoints

interface ClientService {
    @GET(ApiEndPoints.CLIENTS)
    suspend fun clients(): Flow<Page<ClientEntity>>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}")
    suspend fun getClientForId(@Path("clientId") clientId: Long): ClientEntity

    @PUT(ApiEndPoints.CLIENTS + "/{clientId}")
    suspend fun updateClient(
        @Path("clientId") clientId: Long,
        @Body payload: Any,
    ): Flow<Unit>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/images")
    @Headers("Accept: text/plain")
    suspend fun getClientImage(@Path("clientId") clientId: Long): Flow<String>

    @PUT(ApiEndPoints.CLIENTS + "/{clientId}/images")
    suspend fun updateClientImage(
        @Path("clientId") clientId: Long,
        @Body typedFile: String?,
    ): Flow<Unit>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    suspend fun getClientAccounts(@Path("clientId") clientId: Long): Flow<ClientAccounts>

    @GET(ApiEndPoints.CLIENTS + "/{clientId}/accounts")
    suspend fun getAccounts(
        @Path("clientId") clientId: Long,
        @Query("fields") accountType: String,
    ): Flow<ClientAccounts>

    @POST(ApiEndPoints.CLIENTS)
    suspend fun createClient(@Body newClient: NewClient): ClientResponse

    @DELETE(ApiEndPoints.CLIENTS + "/{clientId}")
    suspend fun deleteClient(@Path("clientId") clientId: Int): ClientResponse
}
