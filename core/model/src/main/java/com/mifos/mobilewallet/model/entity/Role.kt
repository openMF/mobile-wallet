package com.mifos.mobilewallet.model.entity

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ankur on 11/June/2018
 */

@Parcelize
data class Role  (
    var id: String?=null,
    @JvmField
    var name: String?=null,
    var description: String?=null,
): Parcelable{
    constructor():this(null,null,null)
}