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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.mapper.toEntity
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.model.network.CommonResponse
import org.mifospay.core.model.network.GenericResponse
import org.mifospay.core.model.network.entity.UserWithRole
import org.mifospay.core.model.network.entity.user.UpdateUserEntityPassword
import org.mifospay.core.model.user.NewUser
import org.mifospay.core.network.SelfServiceApiManager

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

    override suspend fun createUser(newUser: NewUser): Int {
        return withContext(ioDispatcher) {
            selfServiceApiManager.userApi.createUser(newUser.toEntity()).resourceId
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
    ) {
        withContext(ioDispatcher) {
            selfServiceApiManager.userApi.updateUserPassword(
                updateUserEntity = UpdateUserEntityPassword(
                    password,
                    password,
                ),
            )
        }
    }

    override suspend fun deleteUser(userId: Int): CommonResponse {
        return withContext(ioDispatcher) {
            selfServiceApiManager.userApi.deleteUser(userId)
        }
    }

    override suspend fun assignClientToUser(userId: Int, clientId: Int) {
        withContext(ioDispatcher) {
            selfServiceApiManager.userApi.assignClientToUser(userId, mapOf("clients" to listOf(clientId)))
        }
    }
}
