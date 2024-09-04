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
data class SavingsWithAssociations(

    @SerializedName("id")
    var id: Long = 0L,

    @SerializedName("accountNo")
    var accountNo: String? = null,

    @SerializedName("depositType")
    var depositType: DepositType? = null,

    @SerializedName("externalId")
    var externalId: String = " ",

    @SerializedName("clientId")
    var clientId: Int = 0,

    @SerializedName("clientName")
    var clientName: String = " ",

    @SerializedName("savingsProductId")
    var savingsProductId: Int? = null,

    @SerializedName("savingsProductName")
    var savingsProductName: String? = null,

    @SerializedName("fieldOfficerId")
    var fieldOfficerId: Int? = null,

    @SerializedName("status")
    var status: Status? = null,

    @SerializedName("timeline")
    var timeline: TimeLine? = null,

    @SerializedName("currency")
    var currency: Currency? = null,

    @SerializedName("nominalAnnualInterestRate")
    var nominalAnnualInterestRate: Double? = null,

    @SerializedName("minRequiredOpeningBalance")
    var minRequiredOpeningBalance: Double? = null,

    @SerializedName("lockinPeriodFrequency")
    var lockinPeriodFrequency: Double? = null,

    @SerializedName("withdrawalFeeForTransfers")
    var withdrawalFeeForTransfers: Boolean? = null,

    @SerializedName("allowOverdraft")
    var allowOverdraft: Boolean? = null,

    @SerializedName("enforceMinRequiredBalance")
    var enforceMinRequiredBalance: Boolean? = null,

    @SerializedName("withHoldTax")
    var withHoldTax: Boolean? = null,

    @SerializedName("lastActiveTransactionDate")
    var lastActiveTransactionDate: List<Int?>? = null,

    @SerializedName("isDormancyTrackingActive")
    var dormancyTrackingActive: Boolean? = null,

    @SerializedName("summary")
    var summary: Summary? = null,

    @SerializedName("transactions")
    var transactions: List<Transactions> = ArrayList(),

) {
    constructor() : this(
        0L,
        null,
        null,
        " ",
        0,
        " ",
        null,
        null,
        0,
        null,
        null,
        null,
        null,
        null,
        0.0,
        false,
        false,
        false,
        null,
        listOf(),
        null,
        null,
    )
}
