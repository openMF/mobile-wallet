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
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
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
    override suspend fun getUsers(): Flow<DataState<List<UserWithRole>>> {
        return apiManager.userApi.users().asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun getUser(): Flow<DataState<UserWithRole>> {
        return apiManager.userApi.getUser().asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun createUser(newUser: NewUser): DataState<Int> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.userApi.createUser(newUser.toEntity())
            }

            DataState.Success(result.resourceId)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateUser(
        userId: Int,
        updatedUser: NewUser,
    ): Flow<DataState<GenericResponse>> {
        return apiManager.userApi
            .updateUser(userId, updatedUser.toEntity())
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun updateUserPassword(userId: Long, password: String): DataState<String> {
        return try {
            apiManager.userApi.updateUserPassword(
                userId = userId,
                updateUserEntity = UpdateUserEntityPassword(password, password),
            )

            DataState.Success("Password updated successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteUser(userId: Int): DataState<CommonResponse> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.userApi.deleteUser(userId)
            }

            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun assignClientToUser(userId: Int, clientId: Int): DataState<Unit> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.userApi.assignClientToUser(userId, mapOf("clients" to listOf(clientId)))
            }

            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
