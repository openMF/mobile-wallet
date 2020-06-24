package org.mifos.mobilewallet.core.data.fineractcn.entity.depositaccount

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 17/06/2020
 */
data class DepositAccount(
        @SerializedName("customerIdentifier")
        val customerIdentifier: String? = null,
        @SerializedName("productIdentifier")
        val productIdentifier: String? = null,
        @SerializedName("accountIdentifier")
        val accountIdentifier: String? = null,
        @SerializedName("beneficiaries")
        val beneficiaries: List<String> = ArrayList(),
        @SerializedName("state")
        val state: State? = null,
        @SerializedName("balance")
        val balance: Double? = null) {

    enum class State {
        @SerializedName("CREATED")
        CREATED,
        @SerializedName("PENDING")
        PENDING,
        @SerializedName("APPROVED")
        APPROVED,
        @SerializedName("ACTIVE")
        ACTIVE,
        @SerializedName("LOCKED")
        LOCKED,
        @SerializedName("CLOSED")
        CLOSED
    }

}