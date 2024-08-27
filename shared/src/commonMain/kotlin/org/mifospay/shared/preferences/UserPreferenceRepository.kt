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
import org.mifospay.shared.common.proto.UserPreferences
import org.mifospay.shared.modal.domain.UserData

interface UserPreferenceRepository {
    suspend fun updateUserConfig(userConfig: UserData)

    fun getUserConfig(): Flow<UserData>
}

class UserPreferenceRepositoryImpl(
    private val dataStore: DataStore<UserPreferences> = getDataStore(),
) : UserPreferenceRepository {

    override suspend fun updateUserConfig(userConfig: UserData) {
        dataStore.updateData { preferences ->
            preferences.copy(
                auth_token = userConfig.authToken,
                user = userConfig.user,
                user_email = userConfig.userEmail,
                client = userConfig.client,
            )
        }
    }

    override fun getUserConfig(): Flow<UserData> {
        return dataStore.data.map { data ->
            UserData(
                authToken = data.auth_token,
                user = data.user,
                userEmail = data.user_email,
                client = data.client,
            )
        }
    }
}
