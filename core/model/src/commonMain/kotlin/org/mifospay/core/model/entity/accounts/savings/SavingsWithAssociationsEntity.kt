/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.accounts.savings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mifospay.core.model.entity.client.DepositType

@Serializable
data class SavingsWithAssociationsEntity(
    val id: Long = 0L,
    val accountNo: String? = null,
    val depositType: DepositType? = null,
    val externalId: String = "",
    val clientId: Int = 0,
    val clientName: String = "",
    val savingsProductId: Int? = null,
    val savingsProductName: String? = null,
    val fieldOfficerId: Int? = null,
    val status: Status? = null,
    val timeline: TimeLine? = null,
    val currency: Currency? = null,
    val nominalAnnualInterestRate: Double? = null,
    val minRequiredOpeningBalance: Double? = null,
    val lockinPeriodFrequency: Double? = null,
    val withdrawalFeeForTransfers: Boolean? = null,
    val allowOverdraft: Boolean? = null,
    val enforceMinRequiredBalance: Boolean? = null,
    val withHoldTax: Boolean? = null,
    val lastActiveTransactionDate: List<Int?>? = null,
    @SerialName("isDormancyTrackingActive")
    val dormancyTrackingActive: Boolean? = null,
    val summary: Summary? = null,
    val transactions: List<TransactionsEntity> = ArrayList(),
)
