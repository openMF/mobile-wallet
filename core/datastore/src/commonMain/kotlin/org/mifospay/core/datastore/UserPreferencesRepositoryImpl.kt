/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.mifospay.core.common.Result
import org.mifospay.core.datastore.utils.toUserInfo
import org.mifospay.core.model.ClientInfo
import org.mifospay.core.model.UserInfo
import org.mifospay.core.model.domain.client.Client
import org.mifospay.core.model.domain.user.User

class UserPreferencesRepositoryImpl(
    private val preferenceManager: UserPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    unconfinedDispatcher: CoroutineDispatcher,
) : UserPreferencesRepository {
    private val unconfinedScope = CoroutineScope(unconfinedDispatcher)

    override val userInfo: Flow<UserInfo>
        get() = preferenceManager.userInfo.flowOn(ioDispatcher)

    override val token: StateFlow<String?>
        get() = preferenceManager.token.stateIn(
            unconfinedScope,
            initialValue = null,
            started = SharingStarted.Eagerly,
        )

    override val clientInfo: Flow<ClientInfo>
        get() = preferenceManager.clientInfo.flowOn(ioDispatcher)

    override suspend fun updateClientInfo(client: Client): Result<Unit> {
        return try {
            val result = preferenceManager.updateClientInfo(client.toUserInfo())

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUserInfo(user: User): Result<Unit> {
        return try {
            val result = preferenceManager.updateUserInfo(user.toUserInfo())

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun logOut() {
        preferenceManager.clearInfo()
    }
}
