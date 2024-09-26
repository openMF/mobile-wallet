package org.mifospay.core.datastore.proto

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoPreferences(
    val username: String,
    val userId: Int,
    val base64EncodedAuthenticationKey: String,
    val authenticated: Boolean,
    val officeId: Int,
    val officeName: String,
    val roles: List<RolePreferences>,
    val permissions: List<String>,
    val clients: List<Long>,
    val shouldRenewPassword: Boolean,
    val isTwoFactorAuthenticationRequired: Boolean
) {
    companion object {
        val DEFAULT = UserInfoPreferences(
            username = "",
            userId = 0,
            base64EncodedAuthenticationKey = "",
            authenticated = false,
            officeId = 0,
            officeName = "",
            roles = emptyList(),
            permissions = emptyList(),
            clients = emptyList(),
            shouldRenewPassword = false,
            isTwoFactorAuthenticationRequired = false
        )
    }
}
