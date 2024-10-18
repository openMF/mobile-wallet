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

import kotlinx.serialization.Serializable
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class Summary(
    val currency: Currency,
    val totalDeposits: Double = 0.0,
    val totalWithdrawals: Double = 0.0,
    val totalInterestPosted: Long = 0,
    val accountBalance: Double = 0.0,
    val totalOverdraftInterestDerived: Long = 0,
    val interestNotPosted: Long = 0,
    val availableBalance: Double = 0.0,
) : Parcelable

fun Summary.formatAmount(amount: Double): String {
    return CurrencyFormatter.format(
        balance = amount,
        currencyCode = currency.code,
        maximumFractionDigits = null,
    )
}
