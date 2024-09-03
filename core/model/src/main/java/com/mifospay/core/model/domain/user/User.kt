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

import com.mifospay.core.model.entity.Role
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String? = null,
    val userId: Long = 0,
    val base64EncodedAuthenticationKey: String? = null,
    val authenticated: Boolean = false,
    val officeId: Int? = null,
    val officeName: String? = null,
    val roles: List<Role> = emptyList(),
    val permissions: List<String> = emptyList(),
    val clients: List<Long> = emptyList(),
    val shouldRenewPassword: Boolean? = null,
    val isTwoFactorAuthenticationRequired: Boolean? = null,
)
