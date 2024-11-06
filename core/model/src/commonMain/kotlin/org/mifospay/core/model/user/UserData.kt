/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.user

import org.mifospay.core.model.client.Client

data class UserData(
    val token: String,
    val name: String,
    val username: String,
    val email: String,
    val mobileNo: String,
    val userId: Int,
    val clientId: Int,
    val clientVpa: String,
    val accountId: Int,
    val firebaseRegId: String,
    val client: Client,
    val user: UserInfo,
)
