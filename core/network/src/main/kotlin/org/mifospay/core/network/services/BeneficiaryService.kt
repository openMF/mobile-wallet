package org.mifospay.core.network.services

import okhttp3.ResponseBody
import com.mifospay.core.model.entity.beneficary.Beneficiary
import com.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import com.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import com.mifospay.core.model.entity.templates.beneficiary.BeneficiaryTemplate
import org.mifospay.core.network.ApiEndPoints
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
    @get:GET(ApiEndPoints.BENEFICIARIES + "/tpt")
    val beneficiaryList: Observable<List<Beneficiary>>

    @get:GET(ApiEndPoints.BENEFICIARIES + "/tpt/template")
    val beneficiaryTemplate: Observable<BeneficiaryTemplate>

    @POST(ApiEndPoints.BENEFICIARIES + "/tpt")
    fun createBeneficiary(@Body beneficiaryPayload: BeneficiaryPayload): Observable<ResponseBody>

    @PUT(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun updateBeneficiary(
        @Path("beneficiaryId") beneficiaryId: Long,
        @Body payload: BeneficiaryUpdatePayload
    ): Observable<ResponseBody>

    @DELETE(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun deleteBeneficiary(@Path("beneficiaryId") beneficiaryId: Long): Observable<ResponseBody>
}
