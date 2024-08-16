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

import android.os.Parcelable
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import kotlinx.parcelize.Parcelize

@Suppress("MaxLineLength")
@Parcelize
data class Transaction(
    var transactionId: String? = null,
    var clientId: Long = 0,
    var accountId: Long = 0,
    var amount: Double = 0.0,
    var date: String? = null,
    var currency: com.mifospay.core.model.domain.Currency = com.mifospay.core.model.domain.Currency(),
    var transactionType: com.mifospay.core.model.domain.TransactionType = com.mifospay.core.model.domain.TransactionType.OTHER,
    var transferId: Long = 0,
    var transferDetail: TransferDetail = TransferDetail(),
    var receiptId: String? = null,
) : Parcelable {
    constructor() : this(
        "",
        0,
        0,
        0.0,
        "",
        com.mifospay.core.model.domain.Currency(),
        com.mifospay.core.model.domain.TransactionType.OTHER,
        0,
        TransferDetail(),
        "",
    )
}
