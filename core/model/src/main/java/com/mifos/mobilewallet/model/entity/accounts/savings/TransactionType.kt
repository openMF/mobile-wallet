package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/03/17.
 */
@Parcelize
data class TransactionType (
    @SerializedName("id")
    var id: Int?=null,

    @SerializedName("code")
    var code: String?=null,

    @SerializedName("value")
    var value: String?=null,

    @JvmField
    @SerializedName("deposit")
    var deposit: Boolean=false ,

    @SerializedName("dividendPayout")
    var dividendPayout: Boolean=false ,

    @JvmField
    @SerializedName("withdrawal")
    var withdrawal: Boolean=false ,

    @SerializedName("interestPosting")
    var interestPosting: Boolean=false ,

    @SerializedName("feeDeduction")
    var feeDeduction: Boolean=false ,

    @SerializedName("initiateTransfer")
    var initiateTransfer: Boolean=false ,

    @SerializedName("approveTransfer")
    var approveTransfer: Boolean=false ,

    @SerializedName("withdrawTransfer")
    var withdrawTransfer: Boolean=false ,

    @SerializedName("rejectTransfer")
    var rejectTransfer: Boolean=false ,

    @SerializedName("overdraftInterest")
    var overdraftInterest: Boolean=false ,

    @SerializedName("writtenoff")
    var writtenoff: Boolean=false ,

    @SerializedName("overdraftFee")
    var overdraftFee: Boolean=false ,

    @SerializedName("withholdTax")
    var withholdTax: Boolean=false ,

    @SerializedName("escheat")
    var escheat: Boolean?=null
): Parcelable