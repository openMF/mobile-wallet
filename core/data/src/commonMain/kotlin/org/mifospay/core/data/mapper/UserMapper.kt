/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.mapper

import org.mifospay.core.model.user.NewUser
import org.mifospay.core.model.user.RoleInfo
import org.mifospay.core.model.user.UserInfo
import org.mifospay.core.network.model.entity.Role
import org.mifospay.core.network.model.entity.user.NewUserEntity
import org.mifospay.core.network.model.entity.user.User

private const val OFFICE_ID = 1
private const val MOBILE_WALLET_ROLE_ID = 2
private const val SUPER_USER_ROLE_ID = 1

val NEW_USER_ROLE_IDS: ArrayList<Int> = arrayListOf(MOBILE_WALLET_ROLE_ID, SUPER_USER_ROLE_ID)

fun NewUser.toEntity(): NewUserEntity {
    return NewUserEntity(
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

fun User.toUserInfo() = UserInfo(
    username = username,
    userId = userId,
    base64EncodedAuthenticationKey = base64EncodedAuthenticationKey,
    authenticated = authenticated,
    officeId = officeId,
    officeName = officeName,
    roles = roles.map { it.toRoleInfo() },
    permissions = permissions,
    clients = clients,
    shouldRenewPassword = shouldRenewPassword,
    isTwoFactorAuthenticationRequired = isTwoFactorAuthenticationRequired,
)

fun Role.toRoleInfo() = RoleInfo(
    id = id ?: "",
    name = name ?: "",
    description = description ?: "",
    disabled = disabled,
)
