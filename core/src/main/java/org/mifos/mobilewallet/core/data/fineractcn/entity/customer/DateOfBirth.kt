package org.mifos.mobilewallet.core.data.fineractcn.entity.customer

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 17/06/2020
 */
data class DateOfBirth(
        @SerializedName("year")
        val year: Int? = null,
        @SerializedName("month")
        val month: Int? = null,
        @SerializedName("day")
        val day: Int? = null
)