/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.payload

import kotlinx.serialization.Serializable

@Serializable
data class StandingInstructionPayload(
    val fromOfficeId: Int,
    val fromClientId: Int,
    val fromAccountType: Int,
    val name: String?,
    val transferType: Int,
    val priority: Int,
    val status: Int,
    val fromAccountId: Long,
    val toOfficeId: Int,
    val toClientId: Int,
    val toAccountType: Int,
    val toAccountId: Long,
    val instructionType: Int,
    val amount: Double,
    val validFrom: String?,
    val recurrenceType: Int,
    val recurrenceInterval: Int,
    val recurrenceFrequency: Int,
    val locale: String?,
    val dateFormat: String?,
    val validTill: String?,
    val recurrenceOnMonthDay: String?,
    val monthDayFormat: String?,
)
