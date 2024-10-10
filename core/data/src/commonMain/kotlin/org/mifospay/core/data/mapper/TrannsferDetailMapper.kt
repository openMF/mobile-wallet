/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.mapper

import org.mifospay.core.model.savingsaccount.DepositType
import org.mifospay.core.model.savingsaccount.SavingAccount
import org.mifospay.core.model.savingsaccount.Status
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.network.model.entity.accounts.savings.SavingAccountEntity
import org.mifospay.core.network.model.entity.accounts.savings.StatusEntity
import org.mifospay.core.network.model.entity.accounts.savings.TransferDetailEntity
import org.mifospay.core.network.model.entity.client.DepositTypeEntity

fun TransferDetailEntity.toModel(): TransferDetail {
    return TransferDetail(
        id = id,
        fromClient = fromClient.toModel(),
        fromAccount = fromAccount.toModel(),
        toClient = toClient.toModel(),
        toAccount = toAccount.toModel(),
    )
}

fun SavingAccountEntity.toModel(): SavingAccount {
    return SavingAccount(
        id = id,
        accountNo = accountNo,
        productName = productName,
        productId = productId,
        overdraftLimit = overdraftLimit,
        minRequiredBalance = minRequiredBalance,
        accountBalance = accountBalance,
        totalDeposits = totalDeposits,
        savingsProductName = savingsProductName,
        clientName = clientName,
        savingsProductId = savingsProductId,
        nominalAnnualInterestRate = nominalAnnualInterestRate,
        status = status?.toModel(),
        currency = currency.toModel(),
        depositType = depositType?.toModel(),
        isRecurring = this.isRecurring(),
    )
}

fun StatusEntity.toModel(): Status {
    return Status(
        id = id,
        code = code,
        value = value,
        submittedAndPendingApproval = submittedAndPendingApproval,
        approved = approved,
        rejected = rejected,
        withdrawnByApplicant = withdrawnByApplicant,
        active = active,
        closed = closed,
        prematureClosed = prematureClosed,
        transferInProgress = transferInProgress,
        transferOnHold = transferOnHold,
        matured = matured,
    )
}

fun DepositTypeEntity.toModel(): DepositType {
    return DepositType(
        id = id,
        code = code,
        value = value,
    )
}
