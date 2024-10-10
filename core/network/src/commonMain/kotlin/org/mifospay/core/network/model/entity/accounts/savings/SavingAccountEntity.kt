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
import org.mifospay.core.network.model.entity.templates.account.AccountType

@Serializable
data class SavingAccountEntity(
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
    val status: StatusEntity? = null,
    val currency: CurrencyEntity = CurrencyEntity(),
    val depositType: DepositTypeEntity? = null,
    val shortProductName: String? = null,
    val accountType: AccountType = AccountType(),
    val timeline: TimelineEntity = TimelineEntity(),
    val subStatus: SubStatus = SubStatus(),
    val lastActiveTransactionDate: List<Long> = emptyList(),
    val externalId: String? = null,
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
        status = StatusEntity(),
        currency = CurrencyEntity(),
        depositType = DepositTypeEntity(),
        shortProductName = "",
        accountType = AccountType(),
        timeline = TimelineEntity(),
        subStatus = SubStatus(),
        lastActiveTransactionDate = emptyList(),
    )
}

@Serializable
data class SubStatus(
    val id: Long,
    val code: String,
    val value: String,
    val none: Boolean,
    val inactive: Boolean,
    val dormant: Boolean,
    val escheat: Boolean,
    val block: Boolean,
    val blockCredit: Boolean,
    val blockDebit: Boolean,
) {
    constructor() : this(
        id = 0L,
        code = "",
        value = "",
        none = false,
        inactive = false,
        dormant = false,
        escheat = false,
        block = false,
        blockCredit = false,
        blockDebit = false,
    )
}

@Serializable
data class TimelineEntity(
    val submittedOnDate: List<Long> = emptyList(),
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val approvedOnDate: List<Long> = emptyList(),
    val approvedByUsername: String? = null,
    val approvedByFirstname: String? = null,
    val approvedByLastname: String? = null,
    val activatedOnDate: List<Long> = emptyList(),
)
