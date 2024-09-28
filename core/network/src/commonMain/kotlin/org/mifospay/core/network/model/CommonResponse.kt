package org.mifospay.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse(
    val resourceId: Int
)
