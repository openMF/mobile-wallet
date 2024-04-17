package com.mifospay.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: String,
    val name: String,
    @SerialName("state_id")
    val stateId: String
)
