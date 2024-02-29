package com.mifos.mobilewallet.model.entity.client

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Status(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("code")
    var code: String? = null,

    @SerializedName("value")
    var value: String? = null,
) : Parcelable {
    constructor() : this(null, null, null)
}