package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/03/17.
 */
@Parcelize
data class Transactions ( 
    @JvmField
    @SerializedName("id")
    var id: Int?=null,

    @JvmField
    @SerializedName("transactionType")
    var transactionType: TransactionType= TransactionType(),

    @SerializedName("accountId")
    var accountId: Int?=null,

    @SerializedName("accountNo")
    var accountNo: String?=null,

    @SerializedName("date")
    var date: List<Int?> = ArrayList(),

    @JvmField
    @SerializedName("currency")
    var currency: Currency=Currency(),

    @JvmField
    @SerializedName("paymentDetailData")
    var paymentDetailData: PaymentDetailData?=null,

    @JvmField
    @SerializedName("amount")
    var amount: Double=0.0,

    @JvmField
    @SerializedName("transfer")
    var transfer: Transfer =Transfer(),

    @SerializedName("runningBalance")
    var runningBalance: Double?=null,

    @SerializedName("reversed")
    var reversed: Boolean?=null,

    @JvmField
    @SerializedName("submittedOnDate")
    var submittedOnDate: List<Int> = ArrayList<Int>(),

    @SerializedName("interestedPostedAsOn")
    var interestedPostedAsOn: Boolean?=null
): Parcelable {
   constructor() : this(0, TransactionType(), 0, "", ArrayList(), Currency(), PaymentDetailData(), 0.0, Transfer(), 0.0, false, ArrayList(), false)
    }