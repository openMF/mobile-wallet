/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore

import org.mifospay.core.datastore.model.ClientPreferences
import org.mifospay.core.datastore.model.RolePreferences
import org.mifospay.core.datastore.model.UserInfoPreferences
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.user.RoleInfo
import org.mifospay.core.model.user.UserInfo

fun ClientPreferences.toClientInfo(): Client {
    return Client(
        id = id,
        accountNo = accountNo,
        externalId = externalId,
        active = active,
        activationDate = activationDate,
        firstname = firstname,
        lastname = lastname,
        displayName = displayName,
        mobileNo = mobileNo,
        emailAddress = emailAddress,
        dateOfBirth = dateOfBirth,
        isStaff = isStaff,
        officeId = officeId,
        officeName = officeName,
        savingsProductName = savingsProductName,
    )
}

fun Client.toClientPreferences(): ClientPreferences {
    return ClientPreferences(
        id = id,
        accountNo = accountNo,
        externalId = externalId,
        active = active,
        activationDate = activationDate,
        firstname = firstname,
        lastname = lastname,
        displayName = displayName,
        mobileNo = mobileNo,
        emailAddress = emailAddress,
        dateOfBirth = dateOfBirth,
        isStaff = isStaff,
        officeId = officeId,
        officeName = officeName,
        savingsProductName = savingsProductName,
    )
}

fun RolePreferences.toRoleInfo(): RoleInfo {
    return RoleInfo(
        id = id,
        name = name,
        description = description,
        disabled = disabled,
    )
}

fun RoleInfo.toRolePreferences(): RolePreferences {
    return RolePreferences(
        id = id,
        name = name,
        description = description,
        disabled = disabled,
    )
}

fun UserInfoPreferences.toUserInfo(): UserInfo {
    return UserInfo(
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
}

fun UserInfo.toUserInfoPreferences(): UserInfoPreferences {
    return UserInfoPreferences(
        username = username,
        userId = userId,
        base64EncodedAuthenticationKey = base64EncodedAuthenticationKey,
        authenticated = authenticated,
        officeId = officeId,
        officeName = officeName,
        roles = roles.map { it.toRolePreferences() },
        permissions = permissions,
        clients = clients,
        shouldRenewPassword = shouldRenewPassword,
        isTwoFactorAuthenticationRequired = isTwoFactorAuthenticationRequired,
    )
}
