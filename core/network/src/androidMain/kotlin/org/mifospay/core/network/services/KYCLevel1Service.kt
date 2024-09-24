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

import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.GenericResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

interface KYCLevel1Service {
    @POST(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    fun addKYCLevel1Details(
        @Path("clientId") clientId: Int,
        @Body kycLevel1Details: KYCLevel1Details,
    ): Observable<GenericResponse>

    @GET(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}")
    fun fetchKYCLevel1Details(@Path("clientId") clientId: Int): Observable<List<KYCLevel1Details>>

    @PUT(ApiEndPoints.DATATABLES + "/kyc_level1_details/{clientId}/")
    fun updateKYCLevel1Details(
        @Path("clientId") clientId: Int,
        @Body kycLevel1Details: KYCLevel1Details,
    ): Observable<GenericResponse>
}
