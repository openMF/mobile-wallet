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
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
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
    suspend operator fun invoke(username: String, password: String): DataState<UserInfo> {
        val result = withContext(ioDispatcher) {
            repository.authenticate(username, password)
        }

        return when (result) {
            is DataState.Loading -> DataState.Loading
            is DataState.Error -> DataState.Error(Exception("Invalid credentials"))
            is DataState.Success -> {
                if (result.data.clients.isEmpty()) {
                    return DataState.Error(Exception("No clients found"))
                }
                updateUserInfo(result.data)
            }
        }
    }

    private suspend fun updateUserInfo(userInfo: UserInfo): DataState<UserInfo> {
        val updateResult = withContext(ioDispatcher) {
            userPreferencesRepository.updateToken(userInfo.base64EncodedAuthenticationKey)
        }

        return when (updateResult) {
            is DataState.Success -> updateClientInfo(userInfo)
            is DataState.Error -> DataState.Error(Exception("Something went wrong"))
            is DataState.Loading -> DataState.Loading
        }
    }

    private suspend fun updateClientInfo(userInfo: UserInfo): DataState<UserInfo> {
        val clientInfo = withContext(ioDispatcher) {
            clientRepository.getClient(userInfo.clients.first())
        }

        return when (clientInfo) {
            is DataState.Success -> {
                withContext(ioDispatcher) {
                    userPreferencesRepository.updateClientInfo(clientInfo.data)
                    userPreferencesRepository.updateUserInfo(userInfo)
                }

                DataState.Success(userInfo)
            }

            is DataState.Error -> {
                userPreferencesRepository.logOut()
                DataState.Error(Exception("No client found"))
            }

            is DataState.Loading -> DataState.Loading
        }
    }
}
