package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transfer(
    @SerializedName("id")
    var id: Long = 0L,
) : Parcelable {
    constructor() : this(0L)
}
