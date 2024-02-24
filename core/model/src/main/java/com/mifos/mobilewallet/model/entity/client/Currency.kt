package com.mifos.mobilewallet.model.entity.client

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency (
    @SerializedName("code")
    var code: String?=null,

    @SerializedName("name")
    var name: String?=null,

    @SerializedName("decimalPlaces")
    var decimalPlaces: Int?=null,

    @SerializedName("displaySymbol")
    var displaySymbol: String?=null,

    @SerializedName("nameCode")
    var nameCode: String?=null,

    @SerializedName("displayLabel")
    var displayLabel: String?=null,
    ) : Parcelable {
    constructor() : this(null, null, null, null, null, null)
    }