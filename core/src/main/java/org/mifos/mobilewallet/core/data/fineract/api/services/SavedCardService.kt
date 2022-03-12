package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card
import retrofit2.http.*
import rx.Observable

/**
 * Created by ankur on 21/May/2018
 */
interface SavedCardService {
    @POST(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}")
    fun addSavedCard(
        @Path("clientId") clientId: Int,
        @Body card: Card?
    ): Observable<GenericResponse?>?

    @GET(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}")
    fun getSavedCards(@Path("clientId") clientId: Int): Observable<List<Card?>?>?

    @DELETE(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}/{cardId}")
    fun deleteCard(
        @Path("clientId") clientId: Int,
        @Path("cardId") cardId: Int
    ): Observable<GenericResponse?>?

    @PUT(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}/{cardId}")
    fun updateCard(
        @Path("clientId") clientId: Int,
        @Path("cardId") cardId: Int,
        @Body card: Card?
    ): Observable<GenericResponse?>?
}