package com.mifospay.core.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TPTResponse(
    var savingsId: String? = null,
    var resourceId: String? = null,
) : Parcelable
