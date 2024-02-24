package com.mifos.mobilewallet.model.entity.client

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 23/02/17.
 */
@Parcelize
data class Type (
    @SerializedName("id")
    var id: Int?=null,

    @SerializedName("code")
    var code: String?=null,

    @SerializedName("value")
    var value: String?=null,
) : Parcelable {
    constructor() : this(null, null, null)
    }