package org.mifos.mobilewallet.core.data.paymenthub.entity

import com.google.gson.annotations.SerializedName

data class TransactionInfo(@SerializedName("transactionId") val transactionId: String)