/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.model.entity.loanAccount

import kotlinx.serialization.Serializable
import org.mifospay.core.network.model.entity.common.CurrencyResponseDto

@Serializable
data class LoanSummaryResponseDto(
    val principalDisbursed: Double? = null,

    val principalPaid: Double? = null,

    val interestCharged: Double? = null,

    val interestPaid: Double? = null,

    val feeChargesCharged: Double? = null,

    val penaltyChargesCharged: Double? = null,

    val penaltyChargesWaived: Double? = null,

    val totalExpectedRepayment: Double? = null,

    val interestWaived: Double? = null,

    val totalRepayment: Double? = null,

    val feeChargesWaived: Double? = null,

    val totalOutstanding: Double? = null,

    val overdueSinceDate: List<Int>? = null,

    val currency: CurrencyResponseDto? = null,
)
