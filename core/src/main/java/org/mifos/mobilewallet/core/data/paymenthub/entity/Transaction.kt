package org.mifos.mobilewallet.core.data.paymenthub.entity

import com.google.gson.annotations.SerializedName

data class Transaction(@SerializedName("payee") val payee: TransactingEntity,
                       @SerializedName("payer") val payer: TransactingEntity,
                       @SerializedName("amountType") val amountType: String,
                       @SerializedName("amount") val amount: Amount,
                       @SerializedName("transactionType") val transactionType: TransactionType,
                       @SerializedName("note") val note: String)