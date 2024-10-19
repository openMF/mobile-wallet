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

import org.mifospay.core.model.savingsaccount.SavingAccount
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.model.savingsaccount.SavingAccountEntity
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity

fun SavingAccountEntity.toModel(): SavingAccount {
    return SavingAccount(
        id = id,
        accountNo = accountNo,
        productId = productId,
        productName = productName,
        shortProductName = shortProductName,
        status = status,
        currency = currency,
        accountBalance = accountBalance,
        accountType = accountType,
        timeline = timeline,
        subStatus = subStatus,
        lastActiveTransactionDate = lastActiveTransactionDate,
        depositType = depositType,
        externalId = externalId,
    )
}

fun SavingsWithAssociationsEntity.toSavingDetail(): SavingAccountDetail {
    return SavingAccountDetail(
        id = id,
        accountNo = accountNo,
        depositType = depositType,
        clientId = clientId,
        clientName = clientName,
        savingsProductId = savingsProductId,
        savingsProductName = savingsProductName,
        fieldOfficerId = fieldOfficerId,
        status = status,
        timeline = timeline,
        currency = currency,
        nominalAnnualInterestRate = nominalAnnualInterestRate,
        withdrawalFeeForTransfers = withdrawalFeeForTransfers,
        allowOverdraft = allowOverdraft,
        enforceMinRequiredBalance = enforceMinRequiredBalance,
        lienAllowed = lienAllowed,
        withHoldTax = withHoldTax,
        lastActiveTransactionDate = lastActiveTransactionDate,
        isDormancyTrackingActive = isDormancyTrackingActive,
        summary = summary,
        transactions = transactions.map { it.toModel() },
    )
}
