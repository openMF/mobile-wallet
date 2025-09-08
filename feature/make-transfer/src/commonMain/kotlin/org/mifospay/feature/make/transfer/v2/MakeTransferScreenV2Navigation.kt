package org.mifospay.feature.make.transfer.v2

import kotlinx.serialization.Serializable

@Serializable
data class MakeTransferScreenV2Route(
    val clientId: Long,
    val clientName: String,
    val accountNo: String,
    val amount: String,
    val accountId: Long,
)