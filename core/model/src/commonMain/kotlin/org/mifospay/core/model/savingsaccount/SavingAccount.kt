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

@Parcelize
data class SavingAccount(
    val id: Long,
    val accountNo: String,
    val productName: String,
    val productId: Int,
    val overdraftLimit: Long,
    val minRequiredBalance: Long,
    val accountBalance: Double,
    val totalDeposits: Double,
    val savingsProductName: String?,
    val clientName: String?,
    val savingsProductId: String?,
    val nominalAnnualInterestRate: Double,
    val status: Status?,
    val currency: Currency,
    val depositType: DepositType?,
    val isRecurring: Boolean,
) : Parcelable
