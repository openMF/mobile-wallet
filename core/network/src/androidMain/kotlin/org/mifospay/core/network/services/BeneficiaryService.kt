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

import com.mifospay.core.model.entity.beneficary.Beneficiary
import org.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import com.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import com.mifospay.core.model.entity.templates.beneficiary.BeneficiaryTemplate
import okhttp3.ResponseBody
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
    fun createBeneficiary(@Body beneficiaryPayload: org.mifospay.core.model.entity.beneficary.BeneficiaryPayload): Observable<ResponseBody>

    @PUT(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun updateBeneficiary(
        @Path("beneficiaryId") beneficiaryId: Long,
        @Body payload: BeneficiaryUpdatePayload,
    ): Observable<ResponseBody>

    @DELETE(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun deleteBeneficiary(@Path("beneficiaryId") beneficiaryId: Long): Observable<ResponseBody>
}
