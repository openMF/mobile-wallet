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

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.entity.beneficary.Beneficiary
import org.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import org.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import org.mifospay.core.model.entity.templates.beneficiary.BeneficiaryTemplate
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.model.CommonResponse

interface BeneficiaryService {
    @GET(ApiEndPoints.BENEFICIARIES + "/tpt")
    fun beneficiaryList(): Flow<List<Beneficiary>>

    @GET(ApiEndPoints.BENEFICIARIES + "/tpt/template")
    fun beneficiaryTemplate(): Flow<BeneficiaryTemplate>

    @POST(ApiEndPoints.BENEFICIARIES + "/tpt")
    fun createBeneficiary(@Body beneficiaryPayload: BeneficiaryPayload): Flow<CommonResponse>

    @PUT(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun updateBeneficiary(
        @Path("beneficiaryId") beneficiaryId: Long,
        @Body payload: BeneficiaryUpdatePayload,
    ): Flow<CommonResponse>

    @DELETE(ApiEndPoints.BENEFICIARIES + "/tpt/{beneficiaryId}")
    fun deleteBeneficiary(@Path("beneficiaryId") beneficiaryId: Long): Flow<CommonResponse>
}
