/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mifospay.shared.commonMain.proto.Client
import org.mifospay.shared.commonMain.proto.User
import org.mifospay.shared.commonMain.proto.UserPreferences

interface UserPreferenceRepository {
    suspend fun saveToken(token: String)
    fun getToken(): Flow<String>
    suspend fun saveFullName(name: String)
    fun getFullName(): Flow<String>
    suspend fun saveUsername(name: String)
    fun getUsername(): Flow<String>
    suspend fun saveEmail(email: String)
    fun getEmail(): Flow<String>
    suspend fun saveMobile(mobile: String)
    fun getMobile(): Flow<String>
    fun getUserId(): Flow<Int>
    suspend fun setUserId(id: Int)
    fun getClientId(): Flow<Int>
    suspend fun setClientId(clientId: Int)
    fun getClientVpa(): Flow<String>
    suspend fun setClientVpa(vpa: String)
    fun getAccountId(): Flow<Int>
    suspend fun setAccountId(accountId: Int)
    fun getFirebaseRegId(): Flow<String>
    suspend fun setFirebaseRegId(firebaseRegId: String)
    fun getUser(): Flow<User>
    suspend fun setUser(user: User)
    fun getClient(): Flow<Client>
    suspend fun setClient(client: Client)
}

class UserPreferenceRepositoryImpl(
    private val dataStore: DataStore<UserPreferences> = getDataStore(),
) : UserPreferenceRepository {

    override suspend fun saveToken(token: String) {
        dataStore.updateData { preferences ->
            preferences.copy(token = token)
        }
    }

    override fun getToken(): Flow<String> {
        return dataStore.data.map { it.token }
    }

    override suspend fun saveFullName(name: String) {
        dataStore.updateData { preferences ->
            preferences.copy(name = name)
        }
    }

    override fun getFullName(): Flow<String> {
        return dataStore.data.map { it.name }
    }

    override suspend fun saveUsername(name: String) {
        dataStore.updateData { preferences ->
            preferences.copy(username = name)
        }
    }

    override fun getUsername(): Flow<String> {
        return dataStore.data.map { it.username }
    }

    override suspend fun saveEmail(email: String) {
        dataStore.updateData { preferences ->
            preferences.copy(email = email)
        }
    }

    override fun getEmail(): Flow<String> {
        return dataStore.data.map { it.email }
    }

    override suspend fun saveMobile(mobile: String) {
        dataStore.updateData { preferences ->
            preferences.copy(mobile_no = mobile)
        }
    }

    override fun getMobile(): Flow<String> {
        return dataStore.data.map { it.mobile_no }
    }

    override fun getUserId(): Flow<Int> {
        return dataStore.data.map { it.user_id }
    }

    override suspend fun setUserId(id: Int) {
        dataStore.updateData { preferences ->
            preferences.copy(user_id = id)
        }
    }

    override fun getClientId(): Flow<Int> {
        return dataStore.data.map { it.client_id }
    }

    override suspend fun setClientId(clientId: Int) {
        dataStore.updateData { preferences ->
            preferences.copy(client_id = clientId)
        }
    }

    override fun getClientVpa(): Flow<String> {
        return dataStore.data.map { it.client_vpa }
    }

    override suspend fun setClientVpa(vpa: String) {
        dataStore.updateData { preferences ->
            preferences.copy(client_vpa = vpa)
        }
    }

    override fun getAccountId(): Flow<Int> {
        return dataStore.data.map { it.account_id }
    }

    override suspend fun setAccountId(accountId: Int) {
        dataStore.updateData { preferences ->
            preferences.copy(account_id = accountId)
        }
    }

    override fun getFirebaseRegId(): Flow<String> {
        return dataStore.data.map { it.firebase_reg_id }
    }

    override suspend fun setFirebaseRegId(firebaseRegId: String) {
        dataStore.updateData { preferences ->
            preferences.copy(firebase_reg_id = firebaseRegId)
        }
    }

    override fun getUser(): Flow<User> {
        return dataStore.data.map { it.user!! }
    }

    override suspend fun setUser(user: User) {
        dataStore.updateData { preferences ->
            preferences.copy(user = user)
        }
    }

    override fun getClient(): Flow<Client> {
        return dataStore.data.map { it.client!! }
    }

    override suspend fun setClient(client: Client) {
        dataStore.updateData { preferences ->
            preferences.copy(client = client)
        }
    }
}
