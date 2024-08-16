/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    var image: String = "",
    var name: String,
    var number: String,
    var balance: Double = 0.0,
    var id: Long = 0L,
    var productId: Long = 0L,
    var currency: com.mifospay.core.model.domain.Currency,
) : Parcelable
