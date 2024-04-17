package com.mifospay.core.model.domain.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    var name: String? = null,
    var image: String,
    var externalId: String?= null,
    var clientId: Long = 0L,
    var displayName: String,
    var mobileNo: String
) : Parcelable {
    constructor() : this( "", "", "", 0L, "", "")
}
