package org.mifos.mobilewallet.core.data.paymenthub.entity

import com.google.gson.annotations.SerializedName

data class PartyIdentifiers(
        @SerializedName("identifiers")
        val identifierList: List<Identifier>? = null
)