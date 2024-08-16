/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifospay.core.model.entity.register

import com.google.gson.annotations.SerializedName

data class UserVerify(
    @SerializedName("requestId")
    var requestId: String? = null,

    @SerializedName("authenticationToken")
    var authenticationToken: String? = null,
)
