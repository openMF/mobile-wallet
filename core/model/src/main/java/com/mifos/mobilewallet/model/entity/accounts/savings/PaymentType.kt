package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/03/17.
 */
@Parcelize
data class PaymentType ( 
    @SerializedName("id")
    var id: Int?=null,

    @SerializedName("name")
    var name: String?=null,

    ): Parcelable {
    constructor() : this(null, null)
}
