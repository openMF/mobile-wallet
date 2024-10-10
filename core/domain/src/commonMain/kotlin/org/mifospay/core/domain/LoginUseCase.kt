/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import org.mifospay.core.common.Result
import org.mifospay.core.data.repository.AuthenticationRepository
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.user.UserInfo

class LoginUseCase(
    private val repository: AuthenticationRepository,
    private val clientRepository: ClientRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(username: String, password: String): Result<UserInfo> {
        return when (val result = repository.authenticate(username, password)) {
            is Result.Loading -> Result.Loading
            is Result.Error -> Result.Error(Exception("Invalid credentials"))
            is Result.Success -> {
                if (result.data.clients.isEmpty()) {
                    return Result.Error(Exception("No clients found"))
                }
                updateUserInfo(result.data)
            }
        }
    }

    private suspend fun updateUserInfo(userInfo: UserInfo): Result<UserInfo> {
        val updateResult =
            userPreferencesRepository.updateToken(userInfo.base64EncodedAuthenticationKey)
        return when (updateResult) {
            is Result.Success -> updateClientInfo(userInfo)
            is Result.Error -> Result.Error(Exception("Something went wrong"))
            is Result.Loading -> Result.Loading
        }
    }

    private suspend fun updateClientInfo(userInfo: UserInfo): Result<UserInfo> {
        return when (val clientInfo = clientRepository.getClient(userInfo.clients.first())) {
            is Result.Success -> {
                userPreferencesRepository.updateClientInfo(clientInfo.data)
                userPreferencesRepository.updateUserInfo(userInfo)

                Result.Success(userInfo)
            }

            is Result.Error -> {
                userPreferencesRepository.logOut()
                Result.Error(Exception("No client found"))
            }

            is Result.Loading -> Result.Loading
        }
    }
}
