package org.mifos.mobilewallet.core.data.paymenthub.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(@SerializedName("clientRefId") val clientRefId: String,
                       @SerializedName("payee") val payee: TransactingEntity,
                       @SerializedName("payer") val payer: TransactingEntity,
                       @SerializedName("amountType") val amountType: String,
                       @SerializedName("amount") val amount: Amount,
                       @SerializedName("transactionType") val transactionType: TransactionType,
                       @SerializedName("note") val note: String): Parcelable