package org.mifos.mobilewallet.core.data.fineract.api.services

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.Beneficiary
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryPayload
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryUpdatePayload
import org.mifos.mobilewallet.core.data.fineract.entity.templates.beneficiary.BeneficiaryTemplate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

/**
 * Created by dilpreet on 14/6/17.
 */
interface BeneficiaryService {

    @GET("${ApiEndPoints.BENEFICIARIES}/tpt")
    fun getBeneficiaryList(): Observable<List<Beneficiary>>

    @GET("${ApiEndPoints.BENEFICIARIES}/tpt/template")
    fun getBeneficiaryTemplate(): Observable<BeneficiaryTemplate>

    @POST("${ApiEndPoints.BENEFICIARIES}/tpt")
    fun createBeneficiary(@Body beneficiaryPayload: BeneficiaryPayload): Observable<ResponseBody>

    @PUT("${ApiEndPoints.BENEFICIARIES}/tpt/{beneficiaryId}")
    fun updateBeneficiary(@Path("beneficiaryId") beneficiaryId: Long,
                          @Body payload: BeneficiaryUpdatePayload?): Observable<ResponseBody>

    @DELETE("${ApiEndPoints.BENEFICIARIES}/tpt/{beneficiaryId}")
    fun deleteBeneficiary(
            @Path("beneficiaryId") beneficiaryId: Long): Observable<ResponseBody>
}