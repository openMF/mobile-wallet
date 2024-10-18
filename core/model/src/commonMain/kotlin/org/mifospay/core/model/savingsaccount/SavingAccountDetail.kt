/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.savingsaccount

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.model.account.Account

@Parcelize
data class SavingAccountDetail(
    val id: Long,
    val accountNo: String,
    val depositType: DepositType,
    val clientId: Long,
    val clientName: String,
    val savingsProductId: Long,
    val savingsProductName: String,
    val fieldOfficerId: Long,
    val status: Status,
    val timeline: Timeline,
    val currency: Currency,
    val nominalAnnualInterestRate: Double,
    val withdrawalFeeForTransfers: Boolean,
    val allowOverdraft: Boolean,
    val enforceMinRequiredBalance: Boolean,
    val lienAllowed: Boolean,
    val withHoldTax: Boolean,
    val lastActiveTransactionDate: List<Long>,
    val isDormancyTrackingActive: Boolean,
    val summary: Summary,
    val transactions: List<Transaction>,
) : Parcelable

fun SavingAccountDetail.toAccount(): Account {
    return Account(
        name = savingsProductName,
        number = accountNo,
        balance = summary.accountBalance,
        id = id,
        productId = savingsProductId,
        currency = currency,
        status = status,
    )
}
