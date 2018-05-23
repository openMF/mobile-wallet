package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.domain.model.Card;

import java.util.List;

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

    @POST(ApiEndPoints.SAVED_CARD + "/{clientId}")
    Observable<GenericResponse> addSavedCard(
            @Path("clientId") int clientId,
            @Body Card card);

    @GET(ApiEndPoints.SAVED_CARD + "/{clientId}")
    Observable<List<Card>> getSavedCards(@Path("clientId") int clientId);

    @DELETE(ApiEndPoints.SAVED_CARD + "/{clientId}/{cardId}")
    Observable<GenericResponse> deleteCard(@Path("clientId") int clientId,
            @Path("cardId") int cardId);

    @PUT(ApiEndPoints.SAVED_CARD + "/{clientId}/{cardId}")
    Observable<GenericResponse> updateCard(
            @Path("clientId") int clientId,
            @Path("cardId") int cardId,
            @Body Card card);

}
