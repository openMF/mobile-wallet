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
import org.mifospay.core.data.repository.RegistrationRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.entity.register.RegisterPayload
import org.mifospay.core.network.model.entity.register.UserVerify

class RegistrationRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : RegistrationRepository {
    override suspend fun registerUser(registerPayload: RegisterPayload): Result<Unit> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.registrationAPi.registerUser(registerPayload)
            }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun verifyUser(userVerify: UserVerify): Result<Unit> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.registrationAPi.verifyUser(userVerify)
            }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
