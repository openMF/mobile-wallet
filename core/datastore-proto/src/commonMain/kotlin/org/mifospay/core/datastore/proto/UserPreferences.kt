package org.mifospay.core.datastore.proto

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val token: String,
    val name: String,
    val username: String,
    val email: String,
    val mobileNo: String,
    val userId: Int,
    val clientId: Int,
    val clientVpa: String,
    val accountId: Int,
    val firebaseRegId: String,
    val client: ClientPreferences,
    val user: UserInfoPreferences,
) {
    companion object {
        val DEFAULT = UserPreferences(
            token = "",
            name = "",
            username = "",
            email = "",
            mobileNo = "",
            userId = 0,
            clientId = 0,
            clientVpa = "",
            accountId = 0,
            firebaseRegId = "",
            client = ClientPreferences.DEFAULT,
            user = UserInfoPreferences.DEFAULT,
        )
    }
}
