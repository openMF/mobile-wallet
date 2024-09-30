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

import org.mifospay.core.model.domain.Transaction
import org.mifospay.core.model.domain.TransactionType
import org.mifospay.core.model.entity.accounts.savings.SavingsWithAssociationsEntity
import org.mifospay.core.model.entity.accounts.savings.TransactionsEntity
import org.mifospay.core.model.utils.DateHelper

fun SavingsWithAssociationsEntity.toTransactionList(): List<Transaction> {
    return this.transactions.map { it.toModel() }
}

fun TransactionsEntity.toModel(): Transaction {
    return Transaction(
        transactionId = this.id.toString(),
        receiptId = this.paymentDetailData?.receiptNumber,
        amount = this.amount,
        date = DateHelper.getDateAsString(this.submittedOnDate),
        currency = this.currency.toModel(),
        transactionType = when {
            this.transactionType.deposit -> TransactionType.CREDIT
            this.transactionType.withdrawal -> TransactionType.DEBIT
            else -> TransactionType.OTHER
        },
        transferId = this.originalTransactionId ?: 0,
    )
}
