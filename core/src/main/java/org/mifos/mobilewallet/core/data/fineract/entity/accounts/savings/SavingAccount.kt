package org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.mifos.mobilewallet.core.data.fineract.entity.client.DepositType

/**
 * @author Vishwajeet
 * @since 22/06/16
 */
@Parcelize
data class SavingAccount(
        @SerializedName("id")
        var id: Long = 0,

        @SerializedName("accountNo")
        var accountNo: String? = null,

        @SerializedName("productName")
        var productName: String? = null,

        @SerializedName("productId")
        var productId: Int? = null,

        @SerializedName("overdraftLimit")
        var overdraftLimit: Long? = 0,

        @SerializedName("minRequiredBalance")
        var minRequiredBalance: Long? = 0,

        @SerializedName("accountBalance")
        var accountBalance: Double? = 0.0,

        @SerializedName("totalDeposits")
        var totalDeposits: Double? = 0.0,

        @SerializedName("savingsProductName")
        var savingsProductName: String? = null,

        @SerializedName("clientName")
        var clientName: String? = null,

        @SerializedName("savingsProductId")
        var savingsProductId: String? = null,

        @SerializedName("nominalAnnualInterestRate")
        var nominalAnnualInterestRate: Double = 0.0,

        @SerializedName("status")
        var status: Status? = null,

        @SerializedName("currency")
        var currency: Currency? = null,

        @SerializedName("depositType")
        var depositType: DepositType? = null) : Parcelable {

    fun isRecurring(): Boolean
            = depositType?.let { it.isRecurring() } ?: false
}