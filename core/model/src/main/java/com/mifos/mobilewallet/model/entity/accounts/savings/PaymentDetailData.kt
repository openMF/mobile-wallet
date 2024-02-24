package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/03/17.
 */
@Parcelize
data class PaymentDetailData (
    @SerializedName("id")
    var id: Int? =null,

    @SerializedName("paymentType")
    var paymentType: PaymentType? =null,

    @SerializedName("accountNumber")
    var accountNumber: String? =null,

    @SerializedName("checkNumber")
    var checkNumber: String? =null,

    @SerializedName("routingCode")
    var routingCode: String? =null,

    @JvmField
    @SerializedName("receiptNumber")
    var receiptNumber: String? =null,

    @SerializedName("bankNumber")
    var bankNumber: String? =null,
): Parcelable {
    constructor() : this(null, null, null, null, null, null, null)
}
