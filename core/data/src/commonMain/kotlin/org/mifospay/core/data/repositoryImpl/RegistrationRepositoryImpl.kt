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
import kotlinx.coroutines.withContext
import org.mifospay.core.data.repository.RegistrationRepository
import org.mifospay.core.model.network.entity.register.RegisterPayload
import org.mifospay.core.model.network.entity.register.UserVerify
import org.mifospay.core.network.FineractApiManager

class RegistrationRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : RegistrationRepository {
    override suspend fun registerUser(registerPayload: RegisterPayload) {
        withContext(ioDispatcher) {
            apiManager.registrationAPi.registerUser(registerPayload)
        }
    }

    override suspend fun verifyUser(userVerify: UserVerify) {
        withContext(ioDispatcher) {
            apiManager.registrationAPi.verifyUser(userVerify)
        }
    }
}
