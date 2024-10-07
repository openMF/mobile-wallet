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
import kotlinx.coroutines.withContext
import org.mifospay.core.common.Result
import org.mifospay.core.data.repository.AuthenticationRepository
import org.mifospay.core.model.domain.user.User
import org.mifospay.core.model.entity.authentication.AuthenticationPayload
import org.mifospay.core.network.SelfServiceApiManager

class AuthenticationRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : AuthenticationRepository {
    override suspend fun authenticate(username: String, password: String): Result<User> {
        return try {
            val payload = AuthenticationPayload(username, password)

            val result = withContext(ioDispatcher) {
                apiManager.authenticationApi.authenticate(payload)
            }

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
