package com.mifos.mobilewallet.model.domain

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    var image: String="",
    var name: String,
    var number: String,
    var balance: Double=0.0,
    var id: Long=0L,
    var productId: Long=0L,
    var currency: Currency,
) : Parcelable