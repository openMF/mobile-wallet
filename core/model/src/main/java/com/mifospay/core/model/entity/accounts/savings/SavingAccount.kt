/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.accounts.savings

import com.google.gson.annotations.SerializedName
import com.mifospay.core.model.entity.client.DepositType
import kotlinx.serialization.Serializable

@Serializable
data class SavingAccount(

    @SerializedName("id")
    var id: Long = 0L,

    @SerializedName("accountNo")
    var accountNo: String = " ",

    @SerializedName("productName")
    var productName: String = " ",

    @SerializedName("productId")
    var productId: Int = 0,

    @SerializedName("overdraftLimit")
    var overdraftLimit: Long = 0L,

    @SerializedName("minRequiredBalance")
    var minRequiredBalance: Long = 0L,

    @SerializedName("accountBalance")
    var accountBalance: Double = 0.0,

    @SerializedName("totalDeposits")
    var totalDeposits: Double = 0.0,

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
    var currency: Currency = Currency(),

    @SerializedName("depositType")
    var depositType: DepositType? = null,

) {
    fun isRecurring(): Boolean {
        return this.depositType != null && this.depositType!!.isRecurring
    }
    constructor() : this(0L, "", "", 0, 0L, 0L, 0.0, 0.0, "", "", "", 0.0, Status(), Currency(), DepositType())
}
