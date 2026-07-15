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
    val principalDisbursed: Double = 0.0,

    val principalPaid: Double = 0.0,

    val interestCharged: Double = 0.0,

    val interestPaid: Double = 0.0,

    val feeChargesCharged: Double = 0.0,

    val penaltyChargesCharged: Double = 0.0,

    val penaltyChargesWaived: Double = 0.0,

    val totalExpectedRepayment: Double = 0.0,

    val interestWaived: Double = 0.0,

    val totalRepayment: Double = 0.0,

    val feeChargesWaived: Double = 0.0,

    val totalOutstanding: Double = 0.0,

    private val overdueSinceDate: List<Int>? = null,

    val currency: CurrencyResponseDto? = null,
)
