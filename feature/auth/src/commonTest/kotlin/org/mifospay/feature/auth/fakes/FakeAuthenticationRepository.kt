/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.fakes

import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.AuthenticationRepository
import org.mifospay.core.model.user.UserInfo

class FakeAuthenticationRepository(
    var shouldSucceed: Boolean = true,
    private val userInfo: UserInfo = fakeUserInfo,
    private val errorMessage: String = "Authentication failed",
) : AuthenticationRepository {

    override suspend fun authenticate(username: String, password: String): DataState<UserInfo> {
        return if (shouldSucceed) {
            (DataState.Success(userInfo))
        } else {
            DataState.Error(Exception(errorMessage))
        }
    }
}
