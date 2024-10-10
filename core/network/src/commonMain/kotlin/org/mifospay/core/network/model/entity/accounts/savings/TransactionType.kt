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
data class TransactionType(
    val id: Int? = null,
    val code: String? = null,
    val value: String? = null,
    val deposit: Boolean = false,
    val dividendPayout: Boolean = false,
    val withdrawal: Boolean = false,
    val interestPosting: Boolean = false,
    val feeDeduction: Boolean = false,
    val initiateTransfer: Boolean = false,
    val approveTransfer: Boolean = false,
    val withdrawTransfer: Boolean = false,
    val rejectTransfer: Boolean = false,
    val overdraftInterest: Boolean = false,
    val writtenoff: Boolean = false,
    val overdraftFee: Boolean = false,
    val withholdTax: Boolean = false,
    val escheat: Boolean? = null,
    val amountHold: Boolean = false,
    val amountRelease: Boolean = false,
)
