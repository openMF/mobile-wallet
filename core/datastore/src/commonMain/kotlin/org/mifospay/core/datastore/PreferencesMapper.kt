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

import org.mifospay.core.datastore.proto.ClientPreferences
import org.mifospay.core.datastore.proto.RolePreferences
import org.mifospay.core.datastore.proto.UserInfoPreferences
import org.mifospay.core.datastore.proto.UserPreferences
import org.mifospay.core.model.ClientInfo
import org.mifospay.core.model.RoleInfo
import org.mifospay.core.model.UserData
import org.mifospay.core.model.UserInfo

fun ClientPreferences.toClientInfo(): ClientInfo {
    return ClientInfo(
        name = name,
        image = image,
        externalId = externalId,
        clientId = clientId,
        displayName = displayName,
        mobileNo = mobileNo,
    )
}

fun ClientInfo.toClientPreferences(): ClientPreferences {
    return ClientPreferences(
        name = name,
        image = image,
        externalId = externalId,
        clientId = clientId,
        displayName = displayName,
        mobileNo = mobileNo,
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

fun UserPreferences.toUserData(): UserData {
    return UserData(
        token = token,
        name = name,
        username = username,
        email = email,
        mobileNo = mobileNo,
        userId = userId,
        clientId = clientId,
        clientVpa = clientVpa,
        accountId = accountId,
        firebaseRegId = firebaseRegId,
        client = client.toClientInfo(),
        user = user.toUserInfo(),
    )
}

fun UserData.toUserPreferences(): UserPreferences {
    return UserPreferences(
        token = token,
        name = name,
        username = username,
        email = email,
        mobileNo = mobileNo,
        userId = userId,
        clientId = clientId,
        clientVpa = clientVpa,
        accountId = accountId,
        firebaseRegId = firebaseRegId,
        client = client.toClientPreferences(),
        user = user.toUserInfoPreferences(),
    )
}
