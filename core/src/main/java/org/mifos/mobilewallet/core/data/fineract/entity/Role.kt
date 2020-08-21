package org.mifos.mobilewallet.core.data.fineract.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 11/June/2018
 */
@Parcelize
data class Role (
        var id: String? = null,
        var name: String? = null,
        var description: String? = null) : Parcelable