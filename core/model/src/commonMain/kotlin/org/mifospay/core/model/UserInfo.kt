package org.mifospay.core.model

data class UserInfo(
    val username: String,
    val userId: Int,
    val base64EncodedAuthenticationKey: String,
    val authenticated: Boolean,
    val officeId: Int,
    val officeName: String,
    val roles: List<RoleInfo>,
    val permissions: List<String>,
    val clients: List<Long>,
    val shouldRenewPassword: Boolean,
    val isTwoFactorAuthenticationRequired: Boolean
)
