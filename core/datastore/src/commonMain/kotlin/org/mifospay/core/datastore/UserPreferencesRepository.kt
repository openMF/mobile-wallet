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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.common.Result
import org.mifospay.core.model.ClientInfo
import org.mifospay.core.model.UserInfo
import org.mifospay.core.model.domain.client.Client
import org.mifospay.core.model.domain.user.User

interface UserPreferencesRepository {
    val userInfo: Flow<UserInfo>

    val token: StateFlow<String?>

    val clientInfo: Flow<ClientInfo>

    suspend fun updateUserInfo(user: User): Result<Unit>

    suspend fun updateClientInfo(client: Client): Result<Unit>

    suspend fun logOut(): Unit
}
