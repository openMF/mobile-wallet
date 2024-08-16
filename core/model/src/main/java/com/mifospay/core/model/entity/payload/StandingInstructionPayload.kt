/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.payload

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StandingInstructionPayload(
    var fromOfficeId: Int,
    var fromClientId: Int,
    var fromAccountType: Int,
    val name: String?,
    val transferType: Int,
    val priority: Int,
    val status: Int,
    var fromAccountId: Long,
    var toOfficeId: Int,
    var toClientId: Int,
    var toAccountType: Int,
    var toAccountId: Long,
    val instructionType: Int,
    var amount: Double,
    var validFrom: String?,
    val recurrenceType: Int,
    val recurrenceInterval: Int,
    val recurrenceFrequency: Int,
    val locale: String?,
    val dateFormat: String?,
    var validTill: String?,
    var recurrenceOnMonthDay: String?,
    val monthDayFormat: String?,
) : Parcelable
