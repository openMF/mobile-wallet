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
        @Body card: Card
    ): Observable<GenericResponse>

    @GET(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}")
    fun getSavedCards(@Path("clientId") clientId: Int): Observable<List<Card>>

    @DELETE(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}/{cardId}")
    fun deleteCard(
        @Path("clientId") clientId: Int,
        @Path("cardId") cardId: Int
    ): Observable<GenericResponse>

    @PUT(ApiEndPoints.DATATABLES + "/saved_cards/{clientId}/{cardId}")
    fun updateCard(
        @Path("clientId") clientId: Int,
        @Path("cardId") cardId: Int,
        @Body card: Card
    ): Observable<GenericResponse>
}
