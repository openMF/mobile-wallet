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

import kotlinx.serialization.Serializable
import org.mifospay.core.model.entity.client.DepositType

@Serializable
data class SavingAccount(
    val id: Long = 0L,
    val accountNo: String = "",
    val productName: String = "",
    val productId: Int = 0,
    val overdraftLimit: Long = 0L,
    val minRequiredBalance: Long = 0L,
    val accountBalance: Double = 0.0,
    val totalDeposits: Double = 0.0,
    val savingsProductName: String? = null,
    val clientName: String? = null,
    val savingsProductId: String? = null,
    val nominalAnnualInterestRate: Double = 0.0,
    val status: Status? = null,
    val currency: Currency = Currency(),
    val depositType: DepositType? = null,
) {
    fun isRecurring(): Boolean {
        return this.depositType != null && this.depositType.isRecurring
    }

    constructor() : this(
        id = 0L,
        accountNo = "",
        productName = "",
        productId = 0,
        overdraftLimit = 0L,
        minRequiredBalance = 0L,
        accountBalance = 0.0,
        totalDeposits = 0.0,
        savingsProductName = "",
        clientName = "",
        savingsProductId = "",
        nominalAnnualInterestRate = 0.0,
        status = Status(),
        currency = Currency(),
        depositType = DepositType(),
    )
}
