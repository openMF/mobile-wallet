package org.mifos.mobilewallet.core.data.paymenthub.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionStatus(@SerializedName("transferState") val transferState: String,
                             @SerializedName("transactionId") val transactionId: String,
                             @SerializedName("originalRequestData")
                             val originalTransaction: Transaction): Parcelable