/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.accounts.savings

import kotlinx.serialization.Serializable

@Serializable
data class Transactions(
    val id: Int? = null,
    val transactionType: TransactionType = TransactionType(),
    val accountId: Int? = null,
    val accountNo: String? = null,
    val date: List<Int?> = ArrayList(),
    val currency: Currency = Currency(),
    val paymentDetailData: PaymentDetailData? = null,
    val amount: Double = 0.0,
    val transfer: Transfer = Transfer(),
    val runningBalance: Double? = null,
    val reversed: Boolean? = null,
    val submittedOnDate: List<Int> = ArrayList(),
    val interestedPostedAsOn: Boolean? = null,
) {
    constructor() : this(
        id = 0,
        transactionType = TransactionType(),
        accountId = 0,
        accountNo = "",
        date = ArrayList(),
        currency = Currency(),
        paymentDetailData = PaymentDetailData(),
        amount = 0.0,
        transfer = Transfer(),
        runningBalance = 0.0,
        reversed = false,
        submittedOnDate = ArrayList(),
        interestedPostedAsOn = false,
    )
}
