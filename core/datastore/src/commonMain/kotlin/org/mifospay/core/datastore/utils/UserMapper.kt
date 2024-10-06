/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore.utils

import org.mifospay.core.model.ClientInfo
import org.mifospay.core.model.RoleInfo
import org.mifospay.core.model.UserInfo
import org.mifospay.core.model.domain.client.Client
import org.mifospay.core.model.domain.user.User
import org.mifospay.core.model.entity.Role

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

fun Client.toUserInfo() = ClientInfo(
    name = name ?: "",
    image = image ?: "",
    externalId = externalId ?: "",
    clientId = clientId,
    displayName = displayName ?: "",
    mobileNo = mobileNo ?: "",
)
