/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.domain.user

import kotlinx.serialization.Serializable

// Mandatory Fields
// username, firstname, lastname, email, officeId, roles, sendPasswordToEmail
//
// Optional Fields
// staffId,passwordNeverExpires,isSelfServiceUser,clients

@Serializable
class NewUser(
    val username: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val officeId: Int,
    val roles: ArrayList<Int>,
    val sendPasswordToEmail: Boolean,
    val isSelfServiceUser: Boolean,
    val repeatPassword: String,
) {
    constructor(
        username: String,
        firstname: String,
        lastname: String,
        email: String,
        password: String,
    ) : this(
        username = username,
        firstname = firstname,
        lastname = lastname,
        email = email,
        password = password,
        officeId = OFFICE_ID,
        roles = NEW_USER_ROLE_IDS,
        sendPasswordToEmail = false,
        isSelfServiceUser = true,
        repeatPassword = password,
    )
}

private const val OFFICE_ID = 1
private const val MOBILE_WALLET_ROLE_ID = 2
private const val SUPER_USER_ROLE_ID = 1

val NEW_USER_ROLE_IDS: ArrayList<Int> = arrayListOf(MOBILE_WALLET_ROLE_ID, SUPER_USER_ROLE_ID)
