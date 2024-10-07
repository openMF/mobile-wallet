/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.Result
import org.mifospay.core.model.domain.user.NewUser
import org.mifospay.core.model.entity.UserWithRole
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.GenericResponse

interface UserRepository {
    suspend fun getUsers(): Flow<Result<List<UserWithRole>>>

    suspend fun getUser(): Flow<Result<UserWithRole>>

    suspend fun createUser(newUser: NewUser): Result<CommonResponse>

    suspend fun updateUser(userId: Int, updatedUser: NewUser): Flow<Result<GenericResponse>>

    suspend fun deleteUser(userId: Int): Result<CommonResponse>

    suspend fun assignClientToUser(userId: Int, clientId: Int): Result<Unit>
}
