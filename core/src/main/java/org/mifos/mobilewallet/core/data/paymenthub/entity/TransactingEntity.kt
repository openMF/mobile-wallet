package org.mifos.mobilewallet.core.data.paymenthub.entity

import com.google.gson.annotations.SerializedName

data class TransactingEntity(@SerializedName("partyIdInfo") val partyIdInfo: PartyIdInfo,
                             @SerializedName("merchantClassificationCode") val merchantClassificationCode: String,
                             @SerializedName("name") val name: String)