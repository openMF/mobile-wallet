package org.mifos.mobilewallet.core.data.fineractcn.entity.deposit

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 23/06/2020
 */
data class Currency(
        @SerializedName("code")
        val code: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("sign")
        val sign: String? = null,
        @SerializedName("scale")
        val scale: Int? = null
)