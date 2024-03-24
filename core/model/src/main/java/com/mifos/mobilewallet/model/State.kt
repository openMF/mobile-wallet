package com.mifos.mobilewallet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class State(
    val id: String,
    val name: String,
    @SerialName("country_id")
    val countryId: String
)
