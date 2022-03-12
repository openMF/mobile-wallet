package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ankur on 07/June/2018
 */

public interface KYCLevel1Service {

    @POST(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    Observable<GenericResponse> addKYCLevel1Details(
            @Path("clientId") int clientId,
            @Body KYCLevel1Details kycLevel1Details);

    @GET(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    Observable<List<KYCLevel1Details>> fetchKYCLevel1Details(@Path("clientId") int clientId);

    @PUT(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}/")
    Observable<GenericResponse> updateKYCLevel1Details(
            @Path("clientId") int clientId,
            @Body KYCLevel1Details kycLevel1Details);
}
