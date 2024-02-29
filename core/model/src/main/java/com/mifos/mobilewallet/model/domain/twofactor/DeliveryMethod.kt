package com.mifos.mobilewallet.model.domain.twofactor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeliveryMethod(
    var name: String?,
    var target: String?
) : Parcelable
