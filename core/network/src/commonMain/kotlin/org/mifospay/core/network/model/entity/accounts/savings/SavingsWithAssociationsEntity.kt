/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.model.entity.accounts.savings

import kotlinx.serialization.Serializable
import org.mifospay.core.network.model.entity.client.DepositTypeEntity

@Serializable
data class SavingsWithAssociationsEntity(
    val id: Long = 0L,
    val accountNo: String? = null,
    val depositType: DepositTypeEntity? = null,
    val clientId: Int = 0,
    val clientName: String = "",
    val savingsProductId: Int? = null,
    val savingsProductName: String? = null,
    val fieldOfficerId: Int? = null,
    val status: StatusEntity? = null,
    val timeline: TimeLine? = null,
    val currency: CurrencyEntity? = null,
    val nominalAnnualInterestRate: Double? = null,
    val withdrawalFeeForTransfers: Boolean? = null,
    val allowOverdraft: Boolean? = null,
    val enforceMinRequiredBalance: Boolean? = null,
    val withHoldTax: Boolean? = null,
    val lastActiveTransactionDate: List<Int?>? = null,
    val summary: Summary? = null,
    val transactions: List<TransactionsEntity> = ArrayList(),
    val subStatus: SubStatus = SubStatus(),
    val interestCompoundingPeriodType: InterestPeriod = InterestPeriod(),
    val interestPostingPeriodType: InterestPeriod = InterestPeriod(),
    val interestCalculationType: InterestPeriod = InterestPeriod(),
    val interestCalculationDaysInYearType: InterestPeriod = InterestPeriod(),
    val lienAllowed: Boolean = false,
    val isDormancyTrackingActive: Boolean = false,
)

@Serializable
data class InterestPeriod(
    val id: Long = 0,
    val code: String = "",
    val value: String = "",
)
