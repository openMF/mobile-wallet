package org.mifos.mobilewallet.core.data.fineractcn.entity.journal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Created by Devansh on 18/06/2020
 */
@Parcelize
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
        val debtors: @RawValue List<Account>? = null,
        @SerializedName("creditors")
        val creditors: @RawValue List<Account>? = null,
        @SerializedName("state")
        val state: State? = null,
        @SerializedName("message")
        val message: String? = null) : Parcelable {

        enum class State {
                @SerializedName("PENDING")
                PENDING,
                @SerializedName("PROCESSED")
                PROCESSED
        }
}