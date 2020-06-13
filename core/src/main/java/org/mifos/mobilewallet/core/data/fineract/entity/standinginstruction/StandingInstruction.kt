package org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client
import org.mifos.mobilewallet.core.data.fineract.entity.client.Status

/**
 * Created by Devansh 08/06/2020
 */

@Parcelize
data class StandingInstruction(
        val id: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("fromClient")
        val fromClient: Client,
        @SerializedName("fromAccount")
        val fromAccount: SavingAccount,
        @SerializedName("toClient")
        val toClient: Client,
        @SerializedName("toAccount")
        val toAccount: SavingAccount,
        @SerializedName("status")
        val status: Status,
        @SerializedName("amount")
        var amount: Double,
        @SerializedName("validFrom")
        val validFrom: List<Int>,
        @SerializedName("validTill")
        var validTill: List<Int>?,
        @SerializedName("recurrenceInterval")
        var recurrenceInterval: Int,
        @SerializedName("recurrenceOnMonthDay")
        val recurrenceOnMonthDay: List<Int>) : Parcelable