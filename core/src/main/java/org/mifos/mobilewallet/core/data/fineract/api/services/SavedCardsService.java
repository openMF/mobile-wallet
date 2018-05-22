package org.mifos.mobilewallet.core.data.fineract.api.services;

import com.google.gson.JsonArray;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.domain.model.Card;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ankur on 21/May/2018
 */

public interface SavedCardsService {

    @POST(ApiEndPoints.SAVED_CARD + "/{userId}")
    Observable<GenericResponse> addSavedCard(
            @Path("userId") int userId,
            @Body Card card);

    @GET(ApiEndPoints.SAVED_CARD + "/{userId}")
    Observable<JsonArray> getSavedCards(@Path("userId") int userId);

    @DELETE(ApiEndPoints.SAVED_CARD + "/{cardId}")
    Observable<GenericResponse> deleteCard(@Path("cardId") int cardId);

    @PUT(ApiEndPoints.SAVED_CARD + "/{cardId}")
    Observable<GenericResponse> updateCard(
            @Path("cardId") int cardId,
            @Body Card card);

}
