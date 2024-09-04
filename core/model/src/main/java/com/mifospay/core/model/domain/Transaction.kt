/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.domain

import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import kotlinx.serialization.Serializable

@Suppress("MaxLineLength")
@Serializable
data class Transaction(
    var transactionId: String? = null,
    var clientId: Long = 0,
    var accountId: Long = 0,
    var amount: Double = 0.0,
    var date: String? = null,
    var currency: Currency = Currency(),
    var transactionType: TransactionType = TransactionType.OTHER,
    var transferId: Long = 0,
    var transferDetail: TransferDetail = TransferDetail(),
    var receiptId: String? = null,
) {
    constructor() : this(
        "",
        0,
        0,
        0.0,
        "",
        Currency(),
        TransactionType.OTHER,
        0,
        TransferDetail(),
        "",
    )
}
