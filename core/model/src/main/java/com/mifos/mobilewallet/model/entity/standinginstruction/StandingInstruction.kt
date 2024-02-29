package com.mifos.mobilewallet.model.entity.standinginstruction

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount
import com.mifos.mobilewallet.model.entity.client.Client
import com.mifos.mobilewallet.model.entity.client.Status

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
    val recurrenceOnMonthDay: List<Int>
) : Parcelable