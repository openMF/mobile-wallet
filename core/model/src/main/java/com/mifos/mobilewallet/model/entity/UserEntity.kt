package com.mifos.mobilewallet.model.entity

data class UserEntity(
    val username: String,
    val userId: Long = 0,
    val base64EncodedAuthenticationKey: String,
    val authenticated: Boolean = false,
    val officeId: Int,
    val officeName: String,
    val roles: List<Role>,
    val permissions: List<String>,
    val clients: List<Int>,
    val shouldRenewPassword: Boolean,
    val isTwoFactorAuthenticationRequired: Boolean
)
