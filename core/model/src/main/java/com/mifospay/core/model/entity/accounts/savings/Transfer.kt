/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.accounts.savings

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Transfer(
    @SerializedName("id")
    var id: Long = 0L,
) {
    constructor() : this(0L)
}
