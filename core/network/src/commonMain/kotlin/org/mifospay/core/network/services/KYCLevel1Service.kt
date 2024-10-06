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
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.model.entity.kyc.KYCLevel1Details
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.model.GenericResponse

interface KYCLevel1Service {

    @GET(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    suspend fun fetchKYCLevel1Details(@Path("clientId") clientId: Int): Flow<List<KYCLevel1Details>>

    @POST(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    suspend fun addKYCLevel1Details(
        @Path("clientId") clientId: Int,
        @Body kycLevel1Details: KYCLevel1Details,
    ): Flow<GenericResponse>

    @PUT(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}/")
    suspend fun updateKYCLevel1Details(
        @Path("clientId") clientId: Int,
        @Body kycLevel1Details: KYCLevel1Details,
    ): Flow<GenericResponse>
}
