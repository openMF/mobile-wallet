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
import kotlinx.serialization.Serializable

@Serializable
data class Summary(
    @SerializedName("currency")
    var currency: Currency? = null,

    @SerializedName("totalDeposits")
    var totalDeposits: Double? = null,

    @SerializedName("totalWithdrawals")
    var totalWithdrawals: Double? = null,

    @SerializedName("totalInterestEarned")
    var totalInterestEarned: Double? = null,

    @SerializedName("totalInterestPosted")
    var totalInterestPosted: Double? = null,

    @SerializedName("accountBalance")
    var accountBalance: Double? = null,

    @SerializedName("totalOverdraftInterestDerived")
    var totalOverdraftInterestDerived: Double? = null,

    @SerializedName("interestNotPosted")
    var interestNotPosted: Double? = null,

    @SerializedName("lastInterestCalculationDate")
    var lastInterestCalculationDate: List<Int?>? = null,
)
