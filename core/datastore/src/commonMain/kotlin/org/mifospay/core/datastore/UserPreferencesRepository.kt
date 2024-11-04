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
import org.mifospay.core.common.DataState
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.model.user.UserInfo

interface UserPreferencesRepository {
    val userInfo: Flow<UserInfo>

    val token: StateFlow<String?>

    val client: StateFlow<Client?>

    val clientId: StateFlow<Long?>

    val authToken: String?

    val defaultAccount: StateFlow<DefaultAccount?>

    val defaultAccountId: StateFlow<Long?>

    suspend fun updateToken(token: String): DataState<Unit>

    suspend fun updateUserInfo(user: UserInfo): DataState<Unit>

    suspend fun updateClientInfo(client: Client): DataState<Unit>

    suspend fun updateClientProfile(client: UpdatedClient): DataState<Unit>

    suspend fun updateDefaultAccount(account: DefaultAccount): DataState<Unit>

    suspend fun logOut(): Unit
}
