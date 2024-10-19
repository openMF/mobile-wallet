/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.account

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.model.savingsaccount.Currency
import org.mifospay.core.model.savingsaccount.Status

@Parcelize
data class Account(
    val image: String = "",
    val name: String,
    val number: String,
    val balance: Double = 0.0,
    val id: Long = 0L,
    val productId: Long = 0L,
    val currency: Currency,
    val status: Status,
) : Parcelable
