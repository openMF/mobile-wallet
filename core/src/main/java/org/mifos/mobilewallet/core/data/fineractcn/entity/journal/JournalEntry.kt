package org.mifos.mobilewallet.core.data.fineractcn.entity.journal

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 18/06/2020
 */
data class JournalEntry(
        @SerializedName("transactionIdentifier")
        val transactionIdentifier: String? = null,
        @SerializedName("transactionDate")
        val transactionDate: String? = null,
        @SerializedName("transactionType")
        val transactionType: String? = null,
        @SerializedName("clerk")
        val clerk: String? = null,
        @SerializedName("note")
        val note: String? = null,
        @SerializedName("debtors")
        val debtors: List<Account>? = null,
        @SerializedName("creditors")
        val creditors: List<Account>? = null,
        @SerializedName("state")
        val state: State? = null,
        @SerializedName("message")
        val message: String? = null) {

        enum class State {
                @SerializedName("PENDING")
                PENDING,
                @SerializedName("PROCESSED")
                PROCESSED
        }
}