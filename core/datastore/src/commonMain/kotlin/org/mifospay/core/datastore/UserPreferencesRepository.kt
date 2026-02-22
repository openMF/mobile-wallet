package org.mifospay.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.common.DataState
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.model.instance.InterbankServer
import org.mifospay.core.model.instance.ServerInstance
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

    suspend fun updateToken(token: String): DataState<Unit>

    suspend fun updateUserInfo(user: UserInfo): DataState<Unit>

    suspend fun updateClientInfo(client: Client): DataState<Unit>

    suspend fun updateClientProfile(client: UpdatedClient): DataState<Unit>

    suspend fun updateDefaultAccount(account: DefaultAccount): DataState<Unit>

    suspend fun updateSelectedInstance(instance: ServerInstance): DataState<Unit>

    suspend fun updateSelectedInterbankInstance(instance: InterbankServer): DataState<Unit>

    suspend fun updateAccountExternalIds(accountExternalIds: Map<Long, String>): DataState<Unit>

    fun getAccountExternalId(accountId: Long): String?

    suspend fun logOut(): Unit
}