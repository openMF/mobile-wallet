package com.mifos.mobilewallet.model.domain.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var userId: Long=0L,
    var userName: String?=null,
    var authenticationKey: String?=null
) : Parcelable {
    constructor() : this(0L, "", "")
}