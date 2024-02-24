package com.mifos.mobilewallet.model.entity

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ankur on 18/June/2018
 */

@Parcelize
data class TPTResponse (
    var savingsId: String?=null,
    var resourceId: String?=null,
): Parcelable{
    constructor():this(null,null)
}