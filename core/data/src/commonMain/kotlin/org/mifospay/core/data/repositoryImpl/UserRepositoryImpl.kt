/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repositoryImpl

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.mapper.toEntity
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.data.util.parseMifosError
import org.mifospay.core.model.user.NewUser
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.CommonResponse
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.UserWithRole
import org.mifospay.core.network.model.entity.user.UpdateUserEntityPassword

class UserRepositoryImpl(
    private val selfServiceApiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    override suspend fun getUsers(): ScreenStateStream<List<UserWithRole>> {
        return selfServiceApiManager.userApi.users()
            .asScreenStateFlow(isEmpty = { it.isEmpty() })
            .flowOn(ioDispatcher)
    }

    override suspend fun getUser(): ScreenStateStream<UserWithRole> {
        return selfServiceApiManager.userApi.getUser()
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun createUser(newUser: NewUser): DataState<Int> {
        return try {
            val result = withContext(ioDispatcher) {
                selfServiceApiManager.userApi.createUser(newUser.toEntity())
            }

            DataState.Success(result.resourceId)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateUser(
        userId: Int,
        updatedUser: NewUser,
    ): ScreenStateStream<GenericResponse> {
        return selfServiceApiManager.userApi
            .updateUser(updatedUser.toEntity())
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun updateUserPassword(
        userId: Long,
        password: String,
    ): DataState<String> {
        return try {
            selfServiceApiManager.userApi.updateUserPassword(
                updateUserEntity = UpdateUserEntityPassword(
                    password,
                    password,
                ),
            )

            DataState.Success("Password updated successfully")
        } catch (e: ClientRequestException) {
            val message = parseMifosError(
                e.response.bodyAsText(),
                e.response.status.value,
            )

            DataState.Error(Exception(message))
        } catch (e: ServerResponseException) {
            val message = parseMifosError(
                e.response.bodyAsText(),
                e.response.status.value,
            )

            DataState.Error(Exception(message))
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteUser(userId: Int): DataState<CommonResponse> {
        return try {
            val result = withContext(ioDispatcher) {
                selfServiceApiManager.userApi.deleteUser(userId)
            }

            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun assignClientToUser(userId: Int, clientId: Int): DataState<Unit> {
        return try {
            val result = withContext(ioDispatcher) {
                selfServiceApiManager.userApi.assignClientToUser(userId, mapOf("clients" to listOf(clientId)))
            }

            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
