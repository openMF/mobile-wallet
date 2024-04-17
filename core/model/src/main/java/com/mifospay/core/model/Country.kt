package com.mifospay.core.model

import kotlinx.serialization.Serializable


@Serializable
data class Country(
    val id: String,
    val sortname: String,
    val name: String,
)
