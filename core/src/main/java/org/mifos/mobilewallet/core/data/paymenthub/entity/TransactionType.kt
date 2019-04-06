package org.mifos.mobilewallet.core.data.paymenthub.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

import kotlinx.android.parcel.Parcelize

@Parcelize
class TransactionType: Parcelable {

    @SerializedName("scenario")
    var scenario: String? = null

    @SerializedName("initiator")
    var initiator: String? = null

    @SerializedName("initiatorType")
    var initiatorType: String? = null

}
