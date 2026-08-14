/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("TooManyFunctions")

package org.mifos.lib.loan.mapper

import org.mifos.lib.loan.core.model.AccountingRule
import org.mifos.lib.loan.core.model.AmortizationType
import org.mifos.lib.loan.core.model.AmortizationTypeOptions
import org.mifos.lib.loan.core.model.ChargeAppliesTo
import org.mifos.lib.loan.core.model.ChargeCalculationType
import org.mifos.lib.loan.core.model.ChargePaymentMode
import org.mifos.lib.loan.core.model.ChargeTimeType
import org.mifos.lib.loan.core.model.DaysInMonthType
import org.mifos.lib.loan.core.model.DaysInYearType
import org.mifos.lib.loan.core.model.EntityType
import org.mifos.lib.loan.core.model.Frequency
import org.mifos.lib.loan.core.model.InterestCalculationPeriodType
import org.mifos.lib.loan.core.model.InterestRateFrequencyType
import org.mifos.lib.loan.core.model.InterestRateFrequencyTypeOptions
import org.mifos.lib.loan.core.model.InterestRecalculationCompoundingType
import org.mifos.lib.loan.core.model.InterestType
import org.mifos.lib.loan.core.model.InterestTypeOptions
import org.mifos.lib.loan.core.model.RecalculationCompoundingFrequencyType
import org.mifos.lib.loan.core.model.RecalculationRestFrequencyType
import org.mifos.lib.loan.core.model.RepaymentFrequencyDaysOfWeekTypeOptions
import org.mifos.lib.loan.core.model.RepaymentFrequencyNthDayTypeOptions
import org.mifos.lib.loan.core.model.RepaymentFrequencyType
import org.mifos.lib.loan.core.model.RepaymentFrequencyTypeOptions
import org.mifos.lib.loan.core.model.RepeatsOnNthDayOfMonth
import org.mifos.lib.loan.core.model.RescheduleStrategyType
import org.mifos.lib.loan.core.model.TermFrequencyTypeOptions
import org.mifos.lib.loan.core.model.TermPeriodFrequencyType
import org.mifos.lib.loan.core.model.TransactionProcessingStrategyOptions
import org.mifos.lib.loan.core.model.Type
import org.mifos.lib.loan.network.model.TypeResponseDto

fun TypeResponseDto.toTermPeriodFrequencyType() = TermPeriodFrequencyType(id, code, value)

fun TypeResponseDto.toRepaymentFrequencyType() = RepaymentFrequencyType(id, code, value)

fun TypeResponseDto.toInterestRateFrequencyType() = InterestRateFrequencyType(id, code, value)

fun TypeResponseDto.toAmortizationType() = AmortizationType(id, code, value)

fun TypeResponseDto.toInterestType() = InterestType(id, code, value)

fun TypeResponseDto.toInterestCalculationPeriodType() =
    InterestCalculationPeriodType(id, code, value)

fun TypeResponseDto.toDaysInMonthType() = DaysInMonthType(id, code, value)

fun TypeResponseDto.toDaysInYearType() = DaysInYearType(id, code, value)

fun TypeResponseDto.toEntityType() = EntityType(id, code, value)

fun TypeResponseDto.toType() = Type(id, code, value)

fun TypeResponseDto.toFrequency() = Frequency(id, code, value)

fun TypeResponseDto.toRepeatsOnNthDayOfMonth() = RepeatsOnNthDayOfMonth(id, code, value)

fun TypeResponseDto.toInterestRecalculationCompoundingType() =
    InterestRecalculationCompoundingType(id, code, value)

fun TypeResponseDto.toRescheduleStrategyType() = RescheduleStrategyType(id, code, value)

fun TypeResponseDto.toRecalculationRestFrequencyType() =
    RecalculationRestFrequencyType(id, code, value)

fun TypeResponseDto.toRecalculationCompoundingFrequencyType() =
    RecalculationCompoundingFrequencyType(id, code, value)

fun TypeResponseDto.toAccountingRule() = AccountingRule(id, code, value)

fun TypeResponseDto.toChargeAppliesTo() = ChargeAppliesTo(id, code, value)

fun TypeResponseDto.toChargePaymentMode() = ChargePaymentMode(id, code, value)

fun TypeResponseDto.toChargeTimeType() = ChargeTimeType(id = id ?: 0, code = code, value = value)

fun TypeResponseDto.toChargeCalculationType() =
    ChargeCalculationType(id = id ?: 0, code = code, value = value)

fun TypeResponseDto.toTermFrequencyTypeOptions() =
    TermFrequencyTypeOptions(id, code, value)

fun TypeResponseDto.toRepaymentFrequencyTypeOptions() =
    RepaymentFrequencyTypeOptions(id, code, value)

fun TypeResponseDto.toRepaymentFrequencyNthDayTypeOptions() =
    RepaymentFrequencyNthDayTypeOptions(id, code, value)

fun TypeResponseDto.toRepaymentFrequencyDaysOfWeekTypeOptions() =
    RepaymentFrequencyDaysOfWeekTypeOptions(id, code, value)

fun TypeResponseDto.toInterestRateFrequencyTypeOptions() =
    InterestRateFrequencyTypeOptions(id, code, value)

fun TypeResponseDto.toAmortizationTypeOptions() =
    AmortizationTypeOptions(id, code, value)

fun TypeResponseDto.toInterestTypeOptions() =
    InterestTypeOptions(id, code, value)

fun TypeResponseDto.toTransactionProcessingStrategyOptions() =
    TransactionProcessingStrategyOptions(id = id, code = code, name = value)
