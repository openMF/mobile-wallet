/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.model.instance.InterbankServer
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.model.user.Language
import org.mifospay.core.model.user.UserInfo

interface UserPreferencesRepository {
    val userInfo: Flow<UserInfo>

    val token: StateFlow<String?>

    val client: StateFlow<Client?>

    val clientId: StateFlow<Long?>

    val authToken: String?

    val defaultAccount: StateFlow<DefaultAccount?>

    val defaultAccountId: StateFlow<Long?>

    val selectedInstance: StateFlow<ServerInstance?>

    val selectedInterbankInstance: StateFlow<InterbankServer?>

    val accountExternalIds: StateFlow<Map<Long, String>>

    val language: StateFlow<Language>

    suspend fun updateToken(token: String)

    suspend fun updateUserInfo(user: UserInfo)

    suspend fun setLanguage(language: Language)

    suspend fun updateClientInfo(client: Client)

    suspend fun updateClientProfile(client: UpdatedClient)

    suspend fun updateDefaultAccount(account: DefaultAccount)

    suspend fun updateSelectedInstance(instance: ServerInstance)

    suspend fun updateSelectedInterbankInstance(instance: InterbankServer)

    suspend fun updateAccountExternalIds(accountExternalIds: Map<Long, String>)

    fun getAccountExternalId(accountId: Long): String?

    suspend fun logOut(): Unit
}
