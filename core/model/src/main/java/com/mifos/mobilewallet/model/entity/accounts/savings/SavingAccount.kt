package com.mifos.mobilewallet.model.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.mobilewallet.model.entity.client.DepositType
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavingAccount( 
    @JvmField
    @SerializedName("id")
    var id: Long = 0L,

    @JvmField
    @SerializedName("accountNo")
    var accountNo: String=" ",

    @JvmField
    @SerializedName("productName")
    var productName: String= " ",

    @JvmField
    @SerializedName("productId")
    var productId: Int=0 ,

    @SerializedName("overdraftLimit")
    var overdraftLimit: Long = 0L,

    @SerializedName("minRequiredBalance")
    var minRequiredBalance: Long = 0L,

    @JvmField
    @SerializedName("accountBalance")
    var accountBalance: Double = 0.0,

    @SerializedName("totalDeposits")
    var totalDeposits :Double = 0.0,

    @SerializedName("savingsProductName")
    var savingsProductName: String?=null,

    @SerializedName("clientName")
    var clientName: String?=null,

    @SerializedName("savingsProductId")
    var savingsProductId: String?=null,

    @SerializedName("nominalAnnualInterestRate")
    var nominalAnnualInterestRate: Double = 0.0,

    @SerializedName("status")
    var status: Status?=null,

    @JvmField
    @SerializedName("currency")
    var currency: Currency= Currency(),

    @SerializedName("depositType")
    var depositType: DepositType?=null,

): Parcelable {
    fun isRecurring(): Boolean {
        return this.depositType != null && this.depositType!!.isRecurring
    }
    constructor() : this(0L, "", "", 0, 0L, 0L, 0.0, 0.0, "", "", "", 0.0, Status(), Currency(), DepositType())
}
