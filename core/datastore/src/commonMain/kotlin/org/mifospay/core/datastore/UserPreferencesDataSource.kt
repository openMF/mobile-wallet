/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)

package org.mifospay.core.datastore

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import org.mifospay.core.datastore.UserPreferencesDataSource.Companion.DEFAULT_ACCOUNT
import org.mifospay.core.datastore.proto.ClientPreferences
import org.mifospay.core.datastore.proto.UserInfoPreferences
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.model.user.UserInfo

private const val USER_INFO_KEY = "userInfo"
private const val CLIENT_INFO_KEY = "clientInfo"

@OptIn(ExperimentalSerializationApi::class)
class UserPreferencesDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {
    private val _userInfo = MutableStateFlow(
        settings.decodeValue(
            key = USER_INFO_KEY,
            serializer = UserInfoPreferences.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = USER_INFO_KEY,
                serializer = UserInfoPreferences.serializer(),
            ) ?: UserInfoPreferences.DEFAULT,
        ),
    )

    private val _clientInfo = MutableStateFlow(
        settings.decodeValue(
            key = CLIENT_INFO_KEY,
            serializer = ClientPreferences.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = CLIENT_INFO_KEY,
                serializer = ClientPreferences.serializer(),
            ) ?: ClientPreferences.DEFAULT,
        ),
    )

    private val _defaultAccount = MutableStateFlow(
        settings.decodeValue(
            key = DEFAULT_ACCOUNT,
            serializer = DefaultAccount.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = DEFAULT_ACCOUNT,
                serializer = DefaultAccount.serializer(),
            ) ?: DefaultAccount.DEFAULT,
        ),
    )

    val token = _userInfo.map {
        it.base64EncodedAuthenticationKey
    }
    val userInfo = _userInfo.map(UserInfoPreferences::toUserInfo)

    val clientInfo = _clientInfo.map(ClientPreferences::toClientInfo)

    val clientId = _clientInfo.map { it.id }

    val defaultAccount = _defaultAccount.map { it.takeIf { it.accountId != 0L } }

    suspend fun updateClientInfo(client: Client) {
        withContext(dispatcher) {
            settings.putClientPreference(client.toClientPreferences())
            _clientInfo.value = client.toClientPreferences()
        }
    }

    suspend fun updateUserInfo(userInfo: UserInfo) {
        withContext(dispatcher) {
            settings.putUserInfoPreference(userInfo.toUserInfoPreferences())
            _userInfo.value = userInfo.toUserInfoPreferences()
        }
    }

    suspend fun updateClientProfile(client: UpdatedClient) {
        withContext(dispatcher) {
            val updatedClient = _clientInfo.value.copy(
                firstname = client.firstname,
                lastname = client.lastname,
                displayName = client.firstname + " " + client.lastname,
                emailAddress = client.emailAddress,
                mobileNo = client.mobileNo,
                externalId = client.externalId,
            )

            settings.putClientPreference(updatedClient)
            _clientInfo.value = updatedClient
        }
    }

    suspend fun updateToken(token: String) {
        withContext(dispatcher) {
            settings.putUserInfoPreference(
                UserInfoPreferences.DEFAULT.copy(
                    base64EncodedAuthenticationKey = token,
                ),
            )
            _userInfo.value = UserInfoPreferences.DEFAULT.copy(
                base64EncodedAuthenticationKey = token,
            )
        }
    }

    fun updateDefaultAccount(account: DefaultAccount) {
        settings.putDefaultAccount(account)

        _defaultAccount.value = account
    }

    fun updateAuthToken(token: String) {
        settings.putString(AUTH_TOKEN, token)
    }

    fun getAuthToken(): String? {
        return settings.getString(AUTH_TOKEN, "").ifEmpty { null }
    }

    suspend fun clearInfo() {
        withContext(dispatcher) {
            settings.clear()
        }
    }

    companion object {
        const val AUTH_TOKEN = "authToken"
        const val DEFAULT_ACCOUNT = "default_account"
    }
}

private fun Settings.putClientPreference(preference: ClientPreferences) {
    encodeValue(
        key = CLIENT_INFO_KEY,
        serializer = ClientPreferences.serializer(),
        value = preference,
    )
}

private fun Settings.putUserInfoPreference(preference: UserInfoPreferences) {
    encodeValue(
        key = USER_INFO_KEY,
        serializer = UserInfoPreferences.serializer(),
        value = preference,
    )
}

private fun Settings.putDefaultAccount(account: DefaultAccount) {
    encodeValue(
        key = DEFAULT_ACCOUNT,
        serializer = DefaultAccount.serializer(),
        value = account,
    )
}
