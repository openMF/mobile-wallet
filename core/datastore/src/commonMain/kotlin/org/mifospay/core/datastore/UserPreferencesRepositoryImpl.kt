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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mifospay.core.common.DataState
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.model.instance.InterbankServer
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.model.user.Language
import org.mifospay.core.model.user.UserInfo

class UserPreferencesRepositoryImpl(
    private val preferenceManager: UserPreferencesDataSource,
    // manage-pocket linkable-accounts Store5 migration: the pre-migration
    // `pocketPreferencesDataSource` constructor arg (upstream PR #2057
    // multiplatform-settings cache for pocket_accounts / detailed_pocket_accounts /
    // linkable_accounts) is REMOVED. This branch never wired that DataSource
    // into the pocket read path — the Store5 architecture serves the same reads
    // through Room SoT. Logout cache-drain is handled uniformly by
    // `StoreCacheManager.clearAll()` (invoked by `UserLogoutManagerImpl`) which
    // drains every registered store — the `pocket` store's `wallet_pockets`
    // table AND the new `linkableAccounts` store's `wallet_linkable_accounts`
    // table both flush on session end. No separate `clearAllPocketData()` call
    // is needed from `logOut()` below (there is no settings-backed pocket cache
    // to clear anymore).
    private val ioDispatcher: CoroutineDispatcher,
    unconfinedDispatcher: CoroutineDispatcher,
) : UserPreferencesRepository {
    private val unconfinedScope = CoroutineScope(unconfinedDispatcher)

    override val userInfo: Flow<UserInfo>
        get() = preferenceManager.userInfo.flowOn(ioDispatcher)

    override val token: StateFlow<String?>
        get() = preferenceManager.token.stateIn(
            scope = unconfinedScope,
            initialValue = null,
            started = SharingStarted.Eagerly,
        )

    override val client: StateFlow<Client?>
        get() = preferenceManager.clientInfo.stateIn(
            scope = unconfinedScope,
            initialValue = null,
            started = SharingStarted.Eagerly,
        )

    override val clientId: StateFlow<Long?>
        get() = preferenceManager.clientId.stateIn(
            scope = unconfinedScope,
            initialValue = null,
            started = SharingStarted.Eagerly,
        )

    override val authToken: String?
        get() = preferenceManager.getAuthToken()

    override val defaultAccount: StateFlow<DefaultAccount?>
        get() = preferenceManager.defaultAccount.stateIn(
            scope = unconfinedScope,
            initialValue = null,
            started = SharingStarted.Eagerly,
        )

    override val defaultAccountId: StateFlow<Long?>
        get() = preferenceManager.defaultAccount
            .map { it?.accountId }.stateIn(
                scope = unconfinedScope,
                initialValue = null,
                started = SharingStarted.Eagerly,
            )

    override val selectedInstance: StateFlow<ServerInstance?>
        get() = preferenceManager.selectedInstance.stateIn(
            scope = unconfinedScope,
            initialValue = null,
            started = SharingStarted.Eagerly,
        )

    override val selectedInterbankInstance: StateFlow<InterbankServer?>
        get() = preferenceManager.selectedInterbankInstance.stateIn(
            scope = unconfinedScope,
            initialValue = null,
            started = SharingStarted.Eagerly,
        )

    override val accountExternalIds: StateFlow<Map<Long, String>>
        get() = preferenceManager.accountExternalIds.stateIn(
            scope = unconfinedScope,
            initialValue = emptyMap(),
            started = SharingStarted.Eagerly,
        )

    override val language: StateFlow<Language>
        get() = preferenceManager.language.stateIn(
            scope = unconfinedScope,
            initialValue = Language.DEFAULT,
            started = SharingStarted.Eagerly,
        )

    override suspend fun updateDefaultAccount(account: DefaultAccount): DataState<Unit> {
        return try {
            val result = preferenceManager.updateDefaultAccount(account)

            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateSelectedInstance(instance: ServerInstance): DataState<Unit> {
        return try {
            preferenceManager.updateSelectedInstance(instance)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateSelectedInterbankInstance(instance: InterbankServer): DataState<Unit> {
        return try {
            preferenceManager.updateSelectedInterbankInstance(instance)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateAccountExternalIds(accountExternalIds: Map<Long, String>): DataState<Unit> {
        return try {
            preferenceManager.updateAccountExternalIds(accountExternalIds)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override fun getAccountExternalId(accountId: Long): String? {
        return preferenceManager.getAccountExternalId(accountId)
    }

    override suspend fun updateToken(token: String): DataState<Unit> {
        return try {
            val result = preferenceManager.updateAuthToken(token)

            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateClientInfo(client: Client): DataState<Unit> {
        return try {
            val result = preferenceManager.updateClientInfo(client)

            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateClientProfile(client: UpdatedClient): DataState<Unit> {
        return try {
            val result = preferenceManager.updateClientProfile(client)
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun setLanguage(language: Language): DataState<Unit> {
        return try {
            preferenceManager.setLanguage(language)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
    override suspend fun updateUserInfo(user: UserInfo): DataState<Unit> {
        return try {
            val result = preferenceManager.updateUserInfo(user)

            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun logOut() {
        // manage-pocket linkable-accounts Store5 migration: the pre-migration
        // `pocketPreferencesDataSource.clearAllPocketData()` call is REMOVED
        // — see rationale on the constructor arg above. Logout cache-drain
        // for pocket data now happens through `StoreCacheManager.clearAll()`
        // in `UserLogoutManagerImpl.clearUserData()`, which drains BOTH the
        // Room-backed `wallet_pockets` LEDGER (via the `pocket` Store5 store)
        // and the Room-backed `wallet_linkable_accounts` LEDGER (via the
        // new `linkableAccounts` Store5 store).
        preferenceManager.clearInfo()
    }
}
