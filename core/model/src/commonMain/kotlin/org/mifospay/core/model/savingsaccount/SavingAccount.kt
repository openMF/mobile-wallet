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
    val productId: Long,
    val productName: String,
    val shortProductName: String,
    val status: Status,
    val currency: Currency,
    val accountBalance: Double,
    val accountType: AccountType,
    val timeline: Timeline,
    val subStatus: SubStatus,
    val lastActiveTransactionDate: List<Long>,
    val depositType: DepositType?,
    val externalId: String?,
) : Parcelable
