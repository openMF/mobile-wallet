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
import kotlinx.coroutines.withContext
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.mapper.toEntity
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.model.user.NewUser
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.UserWithRole
import org.mifospay.core.network.model.entity.user.UpdateUserEntityPassword

class UserRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    override suspend fun getUsers(): Flow<Result<List<UserWithRole>>> {
        return apiManager.userApi.users().asResult().flowOn(ioDispatcher)
    }

    override suspend fun getUser(): Flow<Result<UserWithRole>> {
        return apiManager.userApi.getUser().asResult().flowOn(ioDispatcher)
    }

    override suspend fun createUser(newUser: NewUser): Result<Int> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.userApi.createUser(newUser.toEntity())
            }

            Result.Success(result.resourceId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUser(
        userId: Int,
        updatedUser: NewUser,
    ): Flow<Result<GenericResponse>> {
        return apiManager.userApi
            .updateUser(userId, updatedUser.toEntity())
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun updateUserPassword(userId: Long, password: String): Result<String> {
        return try {
            apiManager.userApi.updateUserPassword(
                userId = userId,
                updateUserEntity = UpdateUserEntityPassword(password, password),
            )

            Result.Success("Password updated successfully")
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteUser(userId: Int): Result<CommonResponse> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.userApi.deleteUser(userId)
            }

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun assignClientToUser(userId: Int, clientId: Int): Result<Unit> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.userApi.assignClientToUser(userId, mapOf("clients" to listOf(clientId)))
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
