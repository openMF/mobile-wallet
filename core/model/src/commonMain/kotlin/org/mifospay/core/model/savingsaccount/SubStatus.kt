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
data class SubStatus(
    val id: Long,
    val code: String,
    val value: String,
    val none: Boolean,
    val inactive: Boolean,
    val dormant: Boolean,
    val escheat: Boolean,
    val block: Boolean,
    val blockCredit: Boolean,
    val blockDebit: Boolean,
) : Parcelable
