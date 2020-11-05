package org.mifos.mobilewallet.core.data.paymenthub.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 09/07/2020
 */
data class RegistrationEntity(
        @SerializedName("accountId")
        val accountNumber: String?= null,
        @SerializedName("idType")
        val idType: IdentifierType?= null,
        @SerializedName("idValue")
        val idValue: String?= null)