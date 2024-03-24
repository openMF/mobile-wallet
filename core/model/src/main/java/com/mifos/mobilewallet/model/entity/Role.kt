package com.mifos.mobilewallet.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    val disabled: Boolean
) : Parcelable
