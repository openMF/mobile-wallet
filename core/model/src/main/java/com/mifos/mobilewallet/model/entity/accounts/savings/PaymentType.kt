package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentType(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null

) : Parcelable
