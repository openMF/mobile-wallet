package org.mifospay.core.datastore.proto

import kotlinx.serialization.Serializable

@Serializable
data class ClientPreferences(
    val name: String,
    val image: String,
    val externalId: String,
    val clientId: Long,
    val displayName: String,
    val mobileNo: String,
) {
    companion object {
        val DEFAULT = ClientPreferences(
            name = "",
            image = "",
            externalId = "",
            clientId = 0,
            displayName = "",
            mobileNo = "",
        )
    }
}