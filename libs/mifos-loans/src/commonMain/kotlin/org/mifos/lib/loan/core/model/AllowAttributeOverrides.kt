/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AllowAttributeOverrides(

    val amortizationType: Boolean? = null,

    val interestType: Boolean? = null,

    val transactionProcessingStrategyId: Boolean? = null,

    val interestCalculationPeriodType: Boolean? = null,

    val inArrearsTolerance: Boolean? = null,

    val repaymentEvery: Boolean? = null,

    val graceOnPrincipalAndInterestPayment: Boolean? = null,

    val graceOnArrearsAgeing: Boolean? = null,

)
