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

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val accountId: Long,
    val amount: Double,
    val date: String,
    val currency: Currency,
    val transactionType: TransactionType,
    val transactionId: Long,
    val accountNo: String,
    val transferId: Long?,
    val originalTransactionId: Long,
    val paymentDetailId: Long?,
    val description: String = "",
) {
    @Serializable
    data class Type(
        val id: Long,
        val code: String,
        val value: String,
    )
}
