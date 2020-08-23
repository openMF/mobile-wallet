package org.mifos.mobilewallet.core.domain.model.twofactor

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 01/June/2018
 */
@Parcelize
data class DeliveryMethod(
        var name: String? = null,
        var target: String? = null) : Parcelable