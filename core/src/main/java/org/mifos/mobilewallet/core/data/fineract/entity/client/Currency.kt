package org.mifos.mobilewallet.core.data.fineract.entity.client

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Currency(
        @SerializedName("code")
        var code: String? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("decimalPlaces")
        var decimalPlaces: Int? = null,

        @SerializedName("displaySymbol")
        var displaySymbol: String? = null,

        @SerializedName("nameCode")
        var nameCode: String? = null,

        @SerializedName("displayLabel")
        var displayLabel: String? = null) : Parcelable