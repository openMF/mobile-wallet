/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository.auth

import com.mifospay.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mifospay.core.datastore.PreferencesHelper

class AuthenticationUserRepository(
    private val preferencesHelper: PreferencesHelper,
) : UserDataRepository {

    override val userData: Flow<UserData> = flow {
        emit(
            UserData(
                isAuthenticated = !preferencesHelper.token.isNullOrEmpty(),
                userName = preferencesHelper.username,
                // user = preferencesHelper.user,
                clientId = preferencesHelper.clientId,
            ),
        )
    }

    override fun logOut() {
        preferencesHelper.clear()
    }
}
