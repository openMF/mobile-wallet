package com.mifos.mobilewallet.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
    var id: String? = null,
    @JvmField
    var name: String? = null,
    var description: String? = null,
) : Parcelable
