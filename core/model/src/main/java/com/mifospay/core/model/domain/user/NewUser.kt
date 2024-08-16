/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.domain.user

import java.util.Collections

class NewUser(
    val username: String?,
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    val password: String?,
) {
    val officeId = "1"
    val roles: MutableList<Int> = NEW_USER_ROLE_IDS.toMutableList()
    val sendPasswordToEmail = false
    val isSelfServiceUser = true
    val repeatPassword: String? = password
}

private const val MOBILE_WALLET_ROLE_ID = 471
private const val SUPER_USER_ROLE_ID = 1

val NEW_USER_ROLE_IDS: Collection<Int> = Collections.unmodifiableList(
    listOf(MOBILE_WALLET_ROLE_ID, SUPER_USER_ROLE_ID),
)
