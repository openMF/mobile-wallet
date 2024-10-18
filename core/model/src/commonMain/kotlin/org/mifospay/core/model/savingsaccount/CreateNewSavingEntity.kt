/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.savingsaccount

import kotlinx.serialization.Serializable
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Serializable
@Parcelize
data class CreateNewSavingEntity(
    val clientId: String,
    val productId: Long,
    val nominalAnnualInterestRate: Double,
    val minRequiredOpeningBalance: Long,
    val withdrawalFeeForTransfers: Boolean,
    val allowOverdraft: Boolean,
    val overdraftLimit: String,
    val enforceMinRequiredBalance: Boolean,
    val withHoldTax: Boolean,
    val externalId: String,
    val submittedOnDate: String,
    val locale: String,
    val dateFormat: String,
) : Parcelable
