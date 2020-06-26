package org.mifos.mobilewallet.core.data.fineractcn.entity.deposit

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 23/06/2020
 */
data class Product(
        @SerializedName("type")
        val type: String? = null,
        @SerializedName("identifier")
        val identifier: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("description")
        val description: String? = null,
        @SerializedName("currency")
        val currency: Currency? = null,
        @SerializedName("minimumBalance")
        val minimumBalance: Double? = null
)