package org.mifos.mobilewallet.core.data.fineractcn.entity.deposit

import com.google.gson.annotations.SerializedName

/**
 * Created by Devansh on 29/06/2020
 */
data class DepositAccountPayload(
        @SerializedName("customerIdentifier")
        val customerIdentifier: String? = null,
        @SerializedName("productIdentifier")
        val productIdentifier: String? = null,
        @SerializedName("beneficiaries")
        val beneficiaries: List<String> = ArrayList()
)