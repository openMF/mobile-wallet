package org.mifos.mobilewallet.core.data.paymenthub.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Amount: Parcelable {

    @SerializedName("currency")
    var currency: String? = null

    @SerializedName("amount")
    var amount: String? = null
}
