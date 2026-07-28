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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterestRecalculationData(
    val id: Int? = null,

    val loanId: Int? = null,

    val interestRecalculationCompoundingType: InterestRecalculationCompoundingType? = null,

    val rescheduleStrategyType: RescheduleStrategyType? = null,

    val calendarData: CalendarData,

    val recalculationRestFrequencyType: RecalculationRestFrequencyType? = null,

    val recalculationRestFrequencyInterval: Double? = null,

    val recalculationCompoundingFrequencyType: RecalculationCompoundingFrequencyType? = null,

    @SerialName("isCompoundingToBePostedAsTransaction")
    val compoundingToBePostedAsTransaction: Boolean? = null,

    val allowCompoundingOnEod: Boolean? = null,

)
