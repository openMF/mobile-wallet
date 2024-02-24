package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    @JvmField
    @SerializedName("code")
    var code: String= " ",

    @SerializedName("name")
    var name: String= " ",

    @SerializedName("decimalPlaces")
    var decimalPlaces: Int? = null,

    @SerializedName("inMultiplesOf")
    var inMultiplesOf: Int? = null,

    @JvmField
    @SerializedName("displaySymbol")
    var displaySymbol: String= " ",

    @SerializedName("nameCode")
    var nameCode: String= " ",

    @JvmField
    @SerializedName("displayLabel")
    var displayLabel: String= " ",
) : Parcelable {
constructor() : this("", "", 0, 0, "", "", "")
}
