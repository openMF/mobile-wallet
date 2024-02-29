package com.mifos.mobilewallet.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserWithRole(
    var id: String? = null,
    var username: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var email: String? = null,
    var selectedRoles: List<Role>? = ArrayList()
) : Parcelable
