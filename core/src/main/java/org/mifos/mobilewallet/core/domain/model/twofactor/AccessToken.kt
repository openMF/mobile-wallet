package org.mifos.mobilewallet.core.domain.model.twofactor

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 01/June/2018
 */
@Parcelize
class AccessToken(
        var token: String? = null,
        var validFrom: Long? = null,
        var validTo: Long? = null) : Parcelable