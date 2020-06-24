package org.mifos.mobilewallet.core.data.fineractcn.entity.journal

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 18/06/2020
 * Note: This entity is used for both Debtors and Creditors in the FineractCN's documentation
 */
data class Account(
        @SerializedName("accountNumber")
        val accountNumber: String? = null,
        @SerializedName("amount")
        val amount: String? = null)