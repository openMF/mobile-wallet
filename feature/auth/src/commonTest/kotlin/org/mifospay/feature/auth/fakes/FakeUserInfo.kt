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

import org.mifospay.core.model.user.UserInfo

val fakeUserInfo = UserInfo(
    userId = 1,
    username = "abc",
    base64EncodedAuthenticationKey = "fake-auth-key",
    authenticated = true,
    officeId = 1,
    officeName = "Main Office",
    roles = emptyList(),
    permissions = emptyList(),
    clients = listOf(fakeClient.id),
    shouldRenewPassword = false,
    isTwoFactorAuthenticationRequired = false,
)
