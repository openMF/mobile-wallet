package org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Rajan Maurya on 05/03/17.
 */

@Parcelize
data class PaymentDetailData(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("paymentType")
        var paymentType: PaymentType? = null,

        @SerializedName("accountNumber")
        var accountNumber: String? = null,

        @SerializedName("checkNumber")
        var checkNumber: String? = null,

        @SerializedName("routingCode")
        var routingCode: String? = null,

        @SerializedName("receiptNumber")
        var receiptNumber: String? = null,

        @SerializedName("bankNumber")
        var bankNumber: String? = null) : Parcelable