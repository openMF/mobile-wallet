package com.mifos.mobilewallet.model.domain.user

import com.mifos.mobilewallet.model.entity.Role

data class User(
    val username: String,
    val userId: Long = 0,
    val base64EncodedAuthenticationKey: String,
    val authenticated: Boolean = false,
    val officeId: Int,
    val officeName: String,
    val roles: List<Role>,
    val permissions: List<String>,
    val clients: List<Long>,
    val shouldRenewPassword: Boolean,
    val isTwoFactorAuthenticationRequired: Boolean
)
