/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.model.domain.user.NewUser
import org.mifospay.core.model.entity.UserWithRole
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.GenericResponse

class UserRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    override fun getUsers(): Flow<Result<List<UserWithRole>>> {
        return apiManager.userApi.users().asResult().flowOn(ioDispatcher)
    }

    override fun getUser(): Flow<Result<UserWithRole>> {
        return apiManager.userApi.getUser().asResult().flowOn(ioDispatcher)
    }

    override fun createUser(newUser: NewUser): Flow<Result<CommonResponse>> {
        return apiManager.userApi.createUser(newUser).asResult().flowOn(ioDispatcher)
    }

    override fun updateUser(userId: Int, updatedUser: NewUser): Flow<Result<GenericResponse>> {
        return apiManager.userApi.updateUser(userId, updatedUser).asResult().flowOn(ioDispatcher)
    }

    override fun deleteUser(userId: Int): Flow<Result<GenericResponse>> {
        return apiManager.userApi.deleteUser(userId).asResult().flowOn(ioDispatcher)
    }
}
