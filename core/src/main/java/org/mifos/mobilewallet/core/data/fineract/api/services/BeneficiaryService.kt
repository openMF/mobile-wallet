package org.mifos.mobilewallet.core.data.fineract.api.services

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.Beneficiary
import org.mifos.mobilewallet.core.data.fineract.entity.templates.beneficiary.BeneficiaryTemplate
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryPayload
import org.mifos.mobilewallet.core.data.fineract.entity.beneficary.BeneficiaryUpdatePayload
import retrofit2.http.*
import rx.Observable

/**
 * Created by dilpreet on 14/6/17.
 */
interface BeneficiaryService {
    @get:GET(ApiEndPoints.BENEFICIARIES + "/tpt")
    val beneficiaryList: Observable<List<Beneficiary?>?>?

    @get:GET(ApiEndPoints.BENEFICIARIES + "/tpt/template")
    val beneficiaryTemplate: Observable<BeneficiaryTemplate?>?

    @POST(ApiEndPoints.BENEFICIARIES + "/tpt")
    fun createBeneficiary(@Body beneficiaryPayload: BeneficiaryPayload?): Observable<ResponseBody?>?

    @PUT(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun updateBeneficiary(
        @Path("beneficiaryId") beneficiaryId: Long,
        @Body payload: BeneficiaryUpdatePayload?
    ): Observable<ResponseBody?>?

    @DELETE(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun deleteBeneficiary(@Path("beneficiaryId") beneficiaryId: Long): Observable<ResponseBody?>?
}