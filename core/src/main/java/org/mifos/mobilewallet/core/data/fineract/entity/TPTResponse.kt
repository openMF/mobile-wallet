package org.mifos.mobilewallet.core.data.fineract.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 18/June/2018
 */
@Parcelize
data class TPTResponse (
        var savingsId: String? = null,
        var resourceId: String? = null) : Parcelable