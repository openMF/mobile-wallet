package com.mifospay.core.model.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(

    @SerializedName("code")
    var code: String = " ",

    @SerializedName("name")
    var name: String = " ",

    @SerializedName("decimalPlaces")
    var decimalPlaces: Int? = null,

    @SerializedName("inMultiplesOf")
    var inMultiplesOf: Int? = null,

    @SerializedName("displaySymbol")
    var displaySymbol: String = " ",

    @SerializedName("nameCode")
    var nameCode: String = " ",

    @SerializedName("displayLabel")
    var displayLabel: String = " "

) : Parcelable {
    constructor() : this("", "", 0, 0, "", "", "")
}
