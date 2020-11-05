package org.mifos.mobilewallet.core.data.fineractcn.entity.customer

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 17/06/2020
 */
data class Address (
        @SerializedName("street")
        val street: String? = null,
        @SerializedName("city")
        val city: String? = null,
        @SerializedName("region")
        val region: String? = null,
        @SerializedName("postalCode")
        val postalCode: String? = null,
        @SerializedName("countryCode")
        val countryCode: String? = null,
        @SerializedName("country")
        val country: String? = null
)