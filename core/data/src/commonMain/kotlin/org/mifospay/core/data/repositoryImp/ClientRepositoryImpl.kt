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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
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
) : ClientRepository {
    override suspend fun getClients(): Flow<DataState<Page<Client>>> {
        return apiManager.clientsApi.clients().map { it.toModel() }.asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getClientInfo(clientId: Long): Flow<DataState<Client>> {
        return fineractApiManager.clientsApi
            .getClient(clientId)
            .catch { DataState.Error(it, null) }
            .map { DataState.Success(it.toModel()) }
            .flowOn(ioDispatcher)
    }

    override suspend fun getClient(clientId: Long): DataState<Client> {
        return try {
            val result = fineractApiManager.clientsApi.getClientForId(clientId)
            DataState.Success(result.toModel())
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateClient(clientId: Long, client: UpdatedClient): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                fineractApiManager.clientsApi.updateClient(clientId, client.toEntity())
            }

            DataState.Success("Client updated successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override fun getClientImage(clientId: Long): Flow<DataState<String>> {
        return fineractApiManager.clientsApi
            .getClientImage(clientId)
            .catch { DataState.Error(it, null) }
            .map { DataState.Success(it) }
    }

    override suspend fun updateClientImage(clientId: Long, image: String): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                fineractApiManager.clientsApi.updateClientImage(
                    clientId = clientId,
                    typedFile = "data:image/png;base64,$image",
                )
            }

            DataState.Success("Client image updated successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getClientAccounts(clientId: Long): Flow<DataState<ClientAccountsEntity>> {
        return apiManager.clientsApi
            .getClientAccounts(clientId)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun getAccounts(
        clientId: Long,
        accountType: String,
    ): Flow<DataState<List<Account>>> {
        return fineractApiManager.clientsApi
            .getAccounts(clientId, accountType)
            .map { it.toAccount() }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun createClient(newClient: NewClient): DataState<Int> {
        return try {
            val result = withContext(ioDispatcher) {
                fineractApiManager.clientsApi.createClient(newClient.toEntity())
            }

            DataState.Success(result.clientId)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteClient(clientId: Int): DataState<Int> {
        return try {
            val result = withContext(ioDispatcher) {
                fineractApiManager.clientsApi.deleteClient(clientId)
            }

            DataState.Success(result.clientId)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
