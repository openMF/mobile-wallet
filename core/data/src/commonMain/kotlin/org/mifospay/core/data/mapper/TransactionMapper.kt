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

import org.mifospay.core.common.DateHelper
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.model.savingsaccount.TransactionsEntity

fun SavingsWithAssociationsEntity.toTransactionList(): List<Transaction> {
    return this.transactions.map { it.toModel() }
}

fun TransactionsEntity.toModel(): Transaction {
    return Transaction(
        transactionId = this.id,
        amount = this.amount,
        date = DateHelper.getDateAsString(this.submittedOnDate),
        currency = this.currency,
        transactionType = when {
            this.transactionType.deposit -> TransactionType.CREDIT
            this.transactionType.withdrawal -> TransactionType.DEBIT
            else -> TransactionType.OTHER
        },
        transferId = this.transfer?.id,
        accountId = this.accountId,
        accountNo = this.accountNo,
        originalTransactionId = this.originalTransactionId,
        paymentDetailId = this.paymentDetailData?.id,
    )
}
