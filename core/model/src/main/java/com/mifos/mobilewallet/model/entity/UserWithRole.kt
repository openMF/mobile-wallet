package com.mifos.mobilewallet.model.entity

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Created by ankur on 11/June/2018
 */

@Parcelize
data class UserWithRole  (
    var id: String?=null,
    var username: String?=null,
    var firstname: String?=null,
    var lastname: String?=null,
    var email: String?=null,
    @JvmField
    var selectedRoles: @RawValue List<Role>?=ArrayList()
): Parcelable{
    constructor():this(null,null,null,null,null,null)
}