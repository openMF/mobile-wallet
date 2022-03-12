package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import retrofit2.http.*
import rx.Observable

/**
 * Created by ankur on 07/June/2018
 */
interface KYCLevel1Service {
    @POST(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    fun addKYCLevel1Details(
        @Path("clientId") clientId: Int,
        @Body kycLevel1Details: KYCLevel1Details?
    ): Observable<GenericResponse?>?

    @GET(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    fun fetchKYCLevel1Details(@Path("clientId") clientId: Int): Observable<List<KYCLevel1Details?>?>?

    @PUT(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}/")
    fun updateKYCLevel1Details(
        @Path("clientId") clientId: Int,
        @Body kycLevel1Details: KYCLevel1Details?
    ): Observable<GenericResponse?>?
}