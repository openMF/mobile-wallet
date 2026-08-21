/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.mapper.pocket

import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.network.entity.pocket.PocketAccountDto
import org.mifospay.core.model.network.entity.pocket.PocketResponseDto
import org.mifospay.core.model.pocket.PocketAccount

fun PocketResponseDto.toDomainList(): List<PocketAccount> {
    val all = mutableListOf<PocketAccount>()

    loanAccounts.forEach { all.add(it.toDomain(AccountType.LOAN)) }
    savingsAccounts.forEach { all.add(it.toDomain(AccountType.SAVINGS)) }
    shareAccounts.forEach { all.add(it.toDomain(AccountType.SHARE)) }

    return all
}

private fun PocketAccountDto.toDomain(type: AccountType) = PocketAccount(
    id = this.id ?: 0L,
    pocketId = this.pocketId ?: 0L,
    accountId = this.accountId ?: 0L,
    accountType = type,
    accountNumber = this.accountNumber ?: "",
)

fun org.mifospay.core.model.network.entity.loanAccount.LoanStatusResponseDto.toAccountStatus(): org.mifospay.core.model.pocket.AccountStatus =
    when {
        active == true -> org.mifospay.core.model.pocket.AccountStatus.ACTIVE
        pendingApproval == true -> org.mifospay.core.model.pocket.AccountStatus.PENDING
        waitingForDisbursal == true -> org.mifospay.core.model.pocket.AccountStatus.APPROVED
        overpaid == true -> org.mifospay.core.model.pocket.AccountStatus.OVERPAID
        closed == true ||
            closedObligationsMet == true ||
            closedWrittenOff == true ||
            closedRescheduled == true -> org.mifospay.core.model.pocket.AccountStatus.CLOSED
        else -> org.mifospay.core.model.pocket.AccountStatus.UNKNOWN
    }

fun org.mifospay.core.model.savingsaccount.Status.toAccountStatus(): org.mifospay.core.model.pocket.AccountStatus =
    when {
        active == true -> org.mifospay.core.model.pocket.AccountStatus.ACTIVE
        submittedAndPendingApproval == true -> org.mifospay.core.model.pocket.AccountStatus.PENDING
        approved == true -> org.mifospay.core.model.pocket.AccountStatus.APPROVED
        rejected == true -> org.mifospay.core.model.pocket.AccountStatus.REJECTED
        withdrawnByApplicant == true -> org.mifospay.core.model.pocket.AccountStatus.WITHDRAWN
        matured == true -> org.mifospay.core.model.pocket.AccountStatus.MATURED
        closed == true || prematureClosed == true -> org.mifospay.core.model.pocket.AccountStatus.CLOSED
        else -> org.mifospay.core.model.pocket.AccountStatus.UNKNOWN
    }

fun org.mifospay.core.model.network.entity.shareAccount.ShareStatusResponseDto.toAccountStatus(): org.mifospay.core.model.pocket.AccountStatus =
    when {
        active == true -> org.mifospay.core.model.pocket.AccountStatus.ACTIVE
        submittedAndPendingApproval == true -> org.mifospay.core.model.pocket.AccountStatus.PENDING
        approved == true -> org.mifospay.core.model.pocket.AccountStatus.APPROVED
        rejected == true -> org.mifospay.core.model.pocket.AccountStatus.REJECTED
        closed == true -> org.mifospay.core.model.pocket.AccountStatus.CLOSED
        else -> org.mifospay.core.model.pocket.AccountStatus.UNKNOWN
    }
