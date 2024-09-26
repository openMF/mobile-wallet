package org.mifospay.core.model

data class ClientInfo(
    val name: String,
    val image: String,
    val externalId: String,
    val clientId: Long,
    val displayName: String,
    val mobileNo: String,
)
