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
import org.mifospay.core.datastore.proto.ClientPreferences
import org.mifospay.core.datastore.proto.RolePreferences
import org.mifospay.core.datastore.proto.UserInfoPreferences
import org.mifospay.core.datastore.proto.UserPreferences
import org.mifospay.core.model.ClientInfo
import org.mifospay.core.model.RoleInfo
import org.mifospay.core.model.UserData
import org.mifospay.core.model.UserInfo

private const val USER_DATA_KEY = "userData"
private const val USER_INFO_KEY = "userInfo"
private const val CLIENT_INFO_KEY = "clientInfo"
private const val ROLE_INFO_KEY = "roleInfo"

class UserPreferencesDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {
    private val _userData = MutableStateFlow(
        settings.decodeValue(
            key = USER_DATA_KEY,
            serializer = UserPreferences.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = USER_DATA_KEY,
                serializer = UserPreferences.serializer(),
            ) ?: UserPreferences.DEFAULT,
        ),
    )

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

    private val _roleInfo = MutableStateFlow(
        settings.decodeValue(
            key = ROLE_INFO_KEY,
            serializer = RolePreferences.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = ROLE_INFO_KEY,
                serializer = RolePreferences.serializer(),
            ) ?: RolePreferences.DEFAULT,
        ),
    )

    val userData = _userData.map(UserPreferences::toUserData)
    val token = _userData.map(UserPreferences::toUserData).map { it.token }
    val userInfo = _userInfo.map(UserInfoPreferences::toUserInfo)
    val clientInfo = _clientInfo.map(ClientPreferences::toClientInfo)
    val roleInfo = _roleInfo.map(RolePreferences::toRoleInfo)

    suspend fun updateRoleInfo(roleInfo: RoleInfo) {
        withContext(dispatcher) {
            settings.putRolePreference(roleInfo.toRolePreferences())
            _roleInfo.value = roleInfo.toRolePreferences()
        }
    }

    suspend fun updateClientInfo(clientInfo: ClientInfo) {
        withContext(dispatcher) {
            settings.putClientPreference(clientInfo.toClientPreferences())
            _clientInfo.value = clientInfo.toClientPreferences()
        }
    }

    suspend fun updateUserInfo(userInfo: UserInfo) {
        withContext(dispatcher) {
            settings.putUserInfoPreference(userInfo.toUserInfoPreferences())
            _userInfo.value = userInfo.toUserInfoPreferences()
        }
    }

    suspend fun updateUserData(userData: UserData) {
        withContext(dispatcher) {
            settings.putUserPreference(userData.toUserPreferences())
            _userData.value = userData.toUserPreferences()
        }
    }
}

private fun Settings.putClientPreference(preference: ClientPreferences) {
    encodeValue(
        key = CLIENT_INFO_KEY,
        serializer = ClientPreferences.serializer(),
        value = preference,
    )
}

private fun Settings.putRolePreference(preference: RolePreferences) {
    encodeValue(
        key = CLIENT_INFO_KEY,
        serializer = RolePreferences.serializer(),
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

private fun Settings.putUserPreference(preference: UserPreferences) {
    encodeValue(
        key = USER_DATA_KEY,
        serializer = UserPreferences.serializer(),
        value = preference,
    )
}

private fun Settings.getUserPreference(): UserPreferences {
    return decodeValue(
        key = USER_DATA_KEY,
        serializer = UserPreferences.serializer(),
        defaultValue = UserPreferences.DEFAULT,
    )
}
