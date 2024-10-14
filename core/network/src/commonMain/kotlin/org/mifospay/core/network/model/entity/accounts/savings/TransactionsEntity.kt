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

@Serializable
data class TransactionsEntity(
    val id: Long,
    val transactionType: TransactionType,
    val entryType: String,
    val accountId: Long,
    val accountNo: String,
    val date: List<Int>,
    val currency: CurrencyEntity,
    val amount: Double,
    val runningBalance: Double,
    val reversed: Boolean,
    val transfer: Transfer? = null,
    val submittedOnDate: List<Int>,
    val interestedPostedAsOn: Boolean,
    val submittedByUsername: String,
    val isManualTransaction: Boolean,
    val isReversal: Boolean,
    val originalTransactionId: Long,
    val lienTransaction: Boolean,
    val releaseTransactionId: Long,
    val paymentDetailData: PaymentDetailData? = null,
) {
    @Serializable
    data class Currency(
        val code: String,
        val name: String,
        val decimalPlaces: Long,
        val inMultiplesOf: Long,
        val displaySymbol: String,
        val nameCode: String,
        val displayLabel: String,
    )

    @Serializable
    data class TransactionType(
        val id: Long,
        val code: String,
        val value: String,
        val deposit: Boolean,
        val dividendPayout: Boolean,
        val withdrawal: Boolean,
        val interestPosting: Boolean,
        val feeDeduction: Boolean,
        val initiateTransfer: Boolean,
        val approveTransfer: Boolean,
        val withdrawTransfer: Boolean,
        val rejectTransfer: Boolean,
        val overdraftInterest: Boolean,
        val writtenoff: Boolean,
        val overdraftFee: Boolean,
        val withholdTax: Boolean,
        val escheat: Boolean,
        val amountHold: Boolean,
        val amountRelease: Boolean,
    )

    @Serializable
    data class Transfer(
        val id: Long,
        val reversed: Boolean,
        val currency: Currency,
        val transferAmount: Double,
        val transferDate: List<Long>,
        val transferDescription: String,
    )

    @Serializable
    data class PaymentDetailData(
        val id: Long,
        val paymentType: PaymentType,
        val accountNumber: String,
        val checkNumber: String,
        val routingCode: String,
        val receiptNumber: String,
        val bankNumber: String,
    )

    @Serializable
    data class PaymentType(
        val id: Long,
        val name: String,
        val isSystemDefined: Boolean,
    )
}
