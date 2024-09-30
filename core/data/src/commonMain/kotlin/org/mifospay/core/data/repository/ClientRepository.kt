/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.Result
import org.mifospay.core.model.domain.client.Client
import org.mifospay.core.model.domain.client.NewClient
import org.mifospay.core.model.entity.Page
import org.mifospay.core.model.entity.client.ClientAccounts

interface ClientRepository {

    suspend fun getClients(): Flow<Result<Page<Client>>>

    suspend fun getClient(clientId: Long): Flow<Result<Client>>

    suspend fun updateClient(clientId: Long, client: Client): Flow<Result<Unit>>

    suspend fun getClientImage(clientId: Long): Flow<Result<String>>

    suspend fun updateClientImage(clientId: Long, image: String): Flow<Result<Unit>>

    suspend fun getClientAccounts(clientId: Long): Flow<Result<ClientAccounts>>

    suspend fun getAccounts(clientId: Long, accountType: String): Flow<Result<ClientAccounts>>

    suspend fun createClient(newClient: NewClient): Flow<Result<Client>>
}
