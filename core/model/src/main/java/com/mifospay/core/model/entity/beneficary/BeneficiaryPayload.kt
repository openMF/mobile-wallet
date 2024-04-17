package com.mifospay.core.model.entity.beneficary

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BeneficiaryPayload(
    var locale: String? = "en_GB",

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("accountNumber")
    var accountNumber: String? = null,

    @SerializedName("accountType")
    var accountType: Int = 0,

    @SerializedName("transferLimit")
    var transferLimit: Int = 0,

    @SerializedName("officeName")
    var officeName: String? = null,
) : Parcelable {
    constructor() : this(null, null, null, 0, 0, null)
}
