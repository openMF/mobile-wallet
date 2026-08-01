/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import org.mifospay.core.datastore.model.DetailedPocketAccountEntity
import org.mifospay.core.datastore.model.LinkableAccountEntity
import org.mifospay.core.datastore.model.PocketAccountEntity

private const val POCKET_ACCOUNTS_KEY = "pocket_accounts"
private const val DETAILED_POCKET_ACCOUNTS_KEY = "detailed_pocket_accounts"
private const val LINKABLE_ACCOUNTS_KEY = "linkable_accounts"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class PocketPreferencesDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {
    private val _pocketAccounts = MutableStateFlow(
        settings.decodeValue(
            key = POCKET_ACCOUNTS_KEY,
            serializer = ListSerializer(PocketAccountEntity.serializer()),
            defaultValue = emptyList(),
        ),
    )

    private val _detailedPocketAccounts = MutableStateFlow(
        settings.decodeValue(
            key = DETAILED_POCKET_ACCOUNTS_KEY,
            serializer = ListSerializer(DetailedPocketAccountEntity.serializer()),
            defaultValue = emptyList(),
        ),
    )

    private val _linkableAccounts = MutableStateFlow(
        settings.decodeValue(
            key = LINKABLE_ACCOUNTS_KEY,
            serializer = ListSerializer(LinkableAccountEntity.serializer()),
            defaultValue = emptyList(),
        ),
    )

    val pocketAccounts: Flow<List<PocketAccountEntity>> = _pocketAccounts
    val detailedPocketAccounts: Flow<List<DetailedPocketAccountEntity>> = _detailedPocketAccounts
    val linkableAccounts: Flow<List<LinkableAccountEntity>> = _linkableAccounts

    fun getPocketAccountsSync(): List<PocketAccountEntity> = _pocketAccounts.value
    fun getDetailedPocketAccountsSync(): List<DetailedPocketAccountEntity> = _detailedPocketAccounts.value
    fun getLinkableAccountsSync(): List<LinkableAccountEntity> = _linkableAccounts.value

    suspend fun updatePocketAccounts(accounts: List<PocketAccountEntity>) {
        withContext(dispatcher) {
            settings.encodeValue(
                key = POCKET_ACCOUNTS_KEY,
                serializer = ListSerializer(PocketAccountEntity.serializer()),
                value = accounts,
            )
            _pocketAccounts.value = accounts
        }
    }

    suspend fun updateDetailedPocketAccounts(accounts: List<DetailedPocketAccountEntity>) {
        withContext(dispatcher) {
            settings.encodeValue(
                key = DETAILED_POCKET_ACCOUNTS_KEY,
                serializer = ListSerializer(DetailedPocketAccountEntity.serializer()),
                value = accounts,
            )
            _detailedPocketAccounts.value = accounts
        }
    }

    suspend fun updateLinkableAccounts(accounts: List<LinkableAccountEntity>) {
        withContext(dispatcher) {
            settings.encodeValue(
                key = LINKABLE_ACCOUNTS_KEY,
                serializer = ListSerializer(LinkableAccountEntity.serializer()),
                value = accounts,
            )
            _linkableAccounts.value = accounts
        }
    }

    suspend fun clearAllPocketData() {
        withContext(dispatcher) {
            settings.remove(POCKET_ACCOUNTS_KEY)
            settings.remove(DETAILED_POCKET_ACCOUNTS_KEY)
            settings.remove(LINKABLE_ACCOUNTS_KEY)
            _pocketAccounts.value = emptyList()
            _detailedPocketAccounts.value = emptyList()
            _linkableAccounts.value = emptyList()
        }
    }
}
