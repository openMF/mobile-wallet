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
    val id: Int? = null,
    val entryType: String? = null,
    val transactionType: TransactionType = TransactionType(),
    val accountId: Int? = null,
    val accountNo: String? = null,
    val date: List<Int?> = ArrayList(),
    val currency: CurrencyEntity = CurrencyEntity(),
    val paymentDetailData: PaymentDetailData? = null,
    val amount: Double = 0.0,
    val runningBalance: Double? = null,
    val reversed: Boolean? = null,
    val submittedOnDate: List<Int> = ArrayList(),
    val interestedPostedAsOn: Boolean? = null,
    val submittedByUsername: String? = null,
    val isManualTransaction: Boolean = false,
    val isReversal: Boolean = false,
    val originalTransactionId: Long? = null,
    val lienTransaction: Boolean = false,
    val releaseTransactionId: Long? = null,
)
