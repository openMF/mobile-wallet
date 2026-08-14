/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.lib.loan.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LoanAccountApplicationPayloadDto(

    val clientId: Int? = null,

    val productId: Int? = null,

    val productName: String? = null,

    val principal: Double? = null,

    val loanTermFrequency: Int? = null,

    val loanTermFrequencyType: Int? = null,

    val loanType: String? = null,

    val numberOfRepayments: Int? = null,

    val repaymentEvery: Int? = null,

    val repaymentFrequencyType: Int? = null,

    val interestRatePerPeriod: Double? = null,

    val amortizationType: Int? = null,

    val interestType: Int? = null,

    val interestCalculationPeriodType: Int? = null,

    val transactionProcessingStrategyId: Int? = null,

    val transactionProcessingStrategyCode: String? = null,

    val expectedDisbursementDate: String? = null,

    val submittedOnDate: String? = null,

    val linkAccountId: Int? = null,

    val loanPurposeId: Int? = null,

    val loanPurpose: String? = null,

    val maxOutstandingLoanBalance: Double? = null,

    val currency: String? = null,

    val dateFormat: String? = null,

    val locale: String? = null,
)
