/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.domain

import kotlinx.serialization.Serializable
import org.mifospay.core.model.entity.accounts.savings.TransferDetail

@Serializable
data class Transaction(
    val transactionId: String? = null,
    val clientId: Long = 0,
    val accountId: Long = 0,
    val amount: Double = 0.0,
    val date: String? = null,
    val currency: Currency = Currency(),
    val transactionType: TransactionType = TransactionType.OTHER,
    val transferId: Long = 0,
    val transferDetail: TransferDetail = TransferDetail(),
    val receiptId: String? = null,
) {
    constructor() : this(
        transactionId = "",
        clientId = 0,
        accountId = 0,
        amount = 0.0,
        date = "",
        currency = Currency(),
        transactionType = TransactionType.OTHER,
        transferId = 0,
        transferDetail = TransferDetail(),
        receiptId = "",
    )
}
