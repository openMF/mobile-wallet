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

import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.GenericResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

/**
 * Created by ankur on 21/May/2018
 */
interface SavedCardService {
    @POST(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}")
    fun addSavedCard(
        @Path("clientId") clientId: Int,
        @Body card: Card,
    ): Observable<GenericResponse>

    @GET(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}")
    fun getSavedCards(@Path("clientId") clientId: Int): Observable<List<Card>>

    @DELETE(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}/{cardId}")
    fun deleteCard(
        @Path("clientId") clientId: Int,
        @Path("cardId") cardId: Int,
    ): Observable<GenericResponse>

    @PUT(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}/{cardId}")
    fun updateCard(
        @Path("clientId") clientId: Int,
        @Path("cardId") cardId: Int,
        @Body card: Card,
    ): Observable<GenericResponse>
}
