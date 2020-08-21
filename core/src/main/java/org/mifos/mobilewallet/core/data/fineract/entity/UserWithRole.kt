package org.mifos.mobilewallet.core.data.fineract.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ankur on 11/June/2018
 */
@Parcelize
data class UserWithRole (
        var id: String? = null,
        var username: String? = null,
        var firstname: String? = null,
        var lastname: String? = null,
        var email: String? = null,
        var selectedRoles: List<Role>? = null) : Parcelable