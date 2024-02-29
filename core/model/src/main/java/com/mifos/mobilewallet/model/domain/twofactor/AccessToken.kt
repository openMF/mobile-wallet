package com.mifos.mobilewallet.model.domain.twofactor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccessToken(
    var token: String? = null,
    var validFrom: Long? = null,
    var validTo: Long? = null
) : Parcelable

