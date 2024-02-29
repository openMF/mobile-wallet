package com.mifos.mobilewallet.model.entity.client

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepositType(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("code")
    var code: String? = null,

    @SerializedName("value")
    var value: String? = null

) : Parcelable {
    val isRecurring: Boolean
        get() = ServerTypes.RECURRING.id == id
    val endpoint: String
        get() = ServerTypes.fromId(id!!).endpoint
    val serverType: ServerTypes
        get() = ServerTypes.fromId(id!!)


    enum class ServerTypes(val id: Int, val code: String, val endpoint: String) {
        SAVINGS(100, "depositAccountType.savingsDeposit", "savingsaccounts"),
        FIXED(200, "depositAccountType.fixedDeposit", "savingsaccounts"),
        RECURRING(300, "depositAccountType.recurringDeposit", "recurringdepositaccounts");

        companion object {
            fun fromId(id: Int): ServerTypes {
                for (type in entries) {
                    if (type.id == id) {
                        return type
                    }
                }
                return SAVINGS
            }
        }
    }
}