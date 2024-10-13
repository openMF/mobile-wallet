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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.mapper.toAccount
import org.mifospay.core.data.mapper.toEntity
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.NewClient
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.client.ClientAccountsEntity

class ClientRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val fineractApiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
    unconfinedDispatcher: CoroutineDispatcher,
) : ClientRepository {
    private val coroutineScope = CoroutineScope(unconfinedDispatcher)

    override suspend fun getClients(): Flow<Result<Page<Client>>> {
        return apiManager.clientsApi.clients().map { it.toModel() }.asResult().flowOn(ioDispatcher)
    }

    override fun getClientInfo(clientId: Long): StateFlow<Result<Client>> {
        return fineractApiManager.clientsApi
            .getClient(clientId)
            .catch { Result.Error(it) }
            .map { Result.Success(it.toModel()) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = Result.Loading,
            )
    }

    override suspend fun getClient(clientId: Long): Result<Client> {
        return try {
            val result = fineractApiManager.clientsApi.getClientForId(clientId)
            Result.Success(result.toModel())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateClient(clientId: Long, client: UpdatedClient): Result<String> {
        return try {
            withContext(ioDispatcher) {
                fineractApiManager.clientsApi.updateClient(clientId, client.toEntity())
            }

            Result.Success("Client updated successfully")
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getClientImage(clientId: Long): Flow<Result<String>> {
        return fineractApiManager.clientsApi
            .getClientImage(clientId)
            .catch { Result.Error(it) }
            .map { Result.Success(it) }
    }

    override suspend fun updateClientImage(clientId: Long, image: String): Result<String> {
        return try {
            withContext(ioDispatcher) {
                fineractApiManager.clientsApi.updateClientImage(
                    clientId = clientId,
                    typedFile = "data:image/png;base64,$image",
                )
            }

            Result.Success("Client image updated successfully")
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getClientAccounts(clientId: Long): Flow<Result<ClientAccountsEntity>> {
        return apiManager.clientsApi
            .getClientAccounts(clientId)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun getAccounts(
        clientId: Long,
        accountType: String,
    ): Result<List<Account>> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.clientsApi.getAccounts(clientId, accountType)
            }

            Result.Success(result.toAccount())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createClient(newClient: NewClient): Result<Int> {
        return try {
            val result = withContext(ioDispatcher) {
                fineractApiManager.clientsApi.createClient(newClient.toEntity())
            }

            Result.Success(result.clientId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteClient(clientId: Int): Result<Int> {
        return try {
            val result = withContext(ioDispatcher) {
                fineractApiManager.clientsApi.deleteClient(clientId)
            }

            Result.Success(result.clientId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
