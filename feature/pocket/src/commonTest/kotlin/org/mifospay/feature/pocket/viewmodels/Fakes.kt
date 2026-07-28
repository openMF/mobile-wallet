/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.viewmodels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.StringProvider
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.instance.InterbankServer
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.model.payload.PocketLinkPayload
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.model.pocket.PocketAccount
import org.mifospay.core.model.user.UserInfo

class FakeStringProvider(private val value: String = "Unknown Account") : StringProvider {
    override suspend fun get(resource: StringResource, vararg formatArgs: Any): String = value
}

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val _clientId = MutableStateFlow<Long?>(1L)
    override val clientId: StateFlow<Long?> = _clientId

    private val _selectedInstance = MutableStateFlow<ServerInstance?>(null)
    override val selectedInstance: StateFlow<ServerInstance?> = _selectedInstance

    private val _language = MutableStateFlow(org.mifospay.core.model.user.Language.DEFAULT)
    override val language: StateFlow<org.mifospay.core.model.user.Language> = _language

    override suspend fun setLanguage(language: org.mifospay.core.model.user.Language): DataState<Unit> {
        _language.value = language
        return DataState.Success(Unit)
    }

    fun setClientId(id: Long?) {
        _clientId.value = id
    }

    override val userInfo: Flow<UserInfo> = flowOf(
        UserInfo(
            username = "",
            userId = 0L,
            base64EncodedAuthenticationKey = "",
            authenticated = false,
            officeId = 0,
            officeName = "",
            roles = emptyList(),
            permissions = emptyList(),
            clients = emptyList(),
            shouldRenewPassword = false,
            isTwoFactorAuthenticationRequired = false,
        ),
    )
    override val token: StateFlow<String?> = MutableStateFlow(null)
    override val client: StateFlow<Client?> = MutableStateFlow(null)
    override val authToken: String? = null
    override val defaultAccount: StateFlow<DefaultAccount?> = MutableStateFlow(null)
    override val defaultAccountId: StateFlow<Long?> = MutableStateFlow(null)
    override val selectedInterbankInstance: StateFlow<InterbankServer?> = MutableStateFlow(null)
    override val accountExternalIds: StateFlow<Map<Long, String>> = MutableStateFlow(emptyMap())

    override suspend fun updateToken(token: String): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateUserInfo(user: UserInfo): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateClientInfo(client: Client): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateClientProfile(client: UpdatedClient): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateDefaultAccount(account: DefaultAccount): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateSelectedInstance(instance: ServerInstance): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateSelectedInterbankInstance(instance: InterbankServer): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateAccountExternalIds(accountExternalIds: Map<Long, String>): DataState<Unit> = DataState.Success(Unit)
    override fun getAccountExternalId(accountId: Long): String? = null
    override suspend fun logOut() {}
}

class FakePocketRepository : PocketRepository {
    private val pocketAccounts = MutableStateFlow<DataState<List<PocketAccount>>>(DataState.Success(emptyList()))
    private val detailedPocketAccounts = MutableStateFlow<DataState<List<DetailedPocketAccount>>>(DataState.Success(emptyList()))
    private val availableAccounts = MutableStateFlow<DataState<List<LinkableAccount>>>(DataState.Success(emptyList()))

    var linkAccountsResult: DataState<Unit> = DataState.Success(Unit)
    var delinkAccountsResult: DataState<Unit> = DataState.Success(Unit)
    var detailedAccountsAfterLink: DataState<List<DetailedPocketAccount>>? = null
    var detailedAccountsAfterDelink: DataState<List<DetailedPocketAccount>>? = null
    var lastDetailedClientId: Long? = null
    var lastDetailedForceRefresh: Boolean? = null
    var lastAvailableClientId: Long? = null
    var lastLinkPayload: PocketLinkPayload? = null
    var lastExplicitlyAddedAccounts: List<DetailedPocketAccount> = emptyList()
    var lastLinkClientId: Long? = null
    var lastDelinkMappingIds: List<Long> = emptyList()
    var lastDelinkClientId: Long? = null
    var resetPocketCacheCalled: Boolean = false

    fun setDetailedPocketAccounts(state: DataState<List<DetailedPocketAccount>>) {
        detailedPocketAccounts.value = state
    }

    fun setAvailableAccountsToLink(state: DataState<List<LinkableAccount>>) {
        availableAccounts.value = state
    }

    override suspend fun getPocketAccounts(): DataState<List<PocketAccount>> {
        return pocketAccounts.value
    }

    override suspend fun linkAccount(
        accountId: Long,
        accountType: AccountType,
        accountNumber: String,
    ): DataState<Unit> = DataState.Success(Unit)

    override suspend fun resetPocketCache() {
        resetPocketCacheCalled = true
    }

    override fun getDetailedPocketAccounts(
        clientId: Long,
        forceRefresh: Boolean,
    ): Flow<DataState<List<DetailedPocketAccount>>> {
        lastDetailedClientId = clientId
        lastDetailedForceRefresh = forceRefresh
        return detailedPocketAccounts
    }

    override fun getAvailableAccountsToLink(clientId: Long): Flow<DataState<List<LinkableAccount>>> {
        lastAvailableClientId = clientId
        return availableAccounts
    }

    override suspend fun linkAccounts(
        payload: PocketLinkPayload,
        explicitlyAddedAccounts: List<DetailedPocketAccount>,
        clientId: Long,
    ): DataState<Unit> {
        lastLinkPayload = payload
        lastExplicitlyAddedAccounts = explicitlyAddedAccounts
        lastLinkClientId = clientId
        detailedAccountsAfterLink?.let { detailedPocketAccounts.value = it }
        return linkAccountsResult
    }

    override suspend fun delinkAccounts(
        pocketAccountMappingIds: List<Long>,
        clientId: Long,
    ): DataState<Unit> {
        lastDelinkMappingIds = pocketAccountMappingIds
        lastDelinkClientId = clientId
        detailedAccountsAfterDelink?.let { detailedPocketAccounts.value = it }
        return delinkAccountsResult
    }
}
