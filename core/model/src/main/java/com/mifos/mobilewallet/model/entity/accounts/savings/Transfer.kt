package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by ankur on 05/June/2018
 */
@Parcelize
data class Transfer (
    @SerializedName("id")
    var id: Long=0L,
): Parcelable
{
    constructor() : this(0L)
}