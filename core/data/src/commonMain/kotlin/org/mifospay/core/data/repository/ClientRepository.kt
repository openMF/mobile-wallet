package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.NewClient
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.client.ClientAccountsEntity

interface ClientRepository {

    fun getClientInfo(clientId: Long): Flow<DataState<Client>>

    suspend fun getClients(): Flow<DataState<Page<Client>>>

    suspend fun getClient(clientId: Long): DataState<Client>

    suspend fun updateClient(clientId: Long, client: UpdatedClient): DataState<String>

    fun getClientImage(clientId: Long): Flow<DataState<String>>

    suspend fun updateClientImage(clientId: Long, image: String): DataState<String>

    suspend fun getClientAccounts(clientId: Long): ClientAccountsEntity

    suspend fun getAccounts(clientId: Long, accountType: String): Flow<DataState<List<Account>>>

    suspend fun createClient(newClient: NewClient): DataState<Int>

    suspend fun deleteClient(clientId: Int): DataState<Int>
}