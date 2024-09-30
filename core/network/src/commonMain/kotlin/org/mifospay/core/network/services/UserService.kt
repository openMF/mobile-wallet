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
import org.mifospay.core.model.domain.user.NewUser
import org.mifospay.core.model.entity.UserWithRole
import org.mifospay.core.network.ApiEndPoints
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.GenericResponse

interface UserService {
    @GET(ApiEndPoints.USER)
    fun users(): Flow<List<UserWithRole>>

    @POST(ApiEndPoints.USER)
    fun createUser(@Body user: NewUser): Flow<CommonResponse>

    @PUT(ApiEndPoints.USER + "/{userId}")
    fun updateUser(
        @Path("userId") userId: Int,
        @Body updateUserEntity: NewUser,
    ): Flow<GenericResponse>

    @DELETE(ApiEndPoints.USER + "/{userId}")
    fun deleteUser(
        @Path("userId") userId: Int,
    ): Flow<GenericResponse>

    @GET("self/userdetails")
    fun getUser(): Flow<UserWithRole>
}
