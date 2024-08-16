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
data class BankAccountDetails(
    var bankName: String? = null,
    var accountholderName: String? = null,
    var branch: String? = null,
    var ifsc: String? = null,
    var type: String? = null,
    var isUpiEnabled: Boolean = false,
    var upiPin: String? = null,
) : Parcelable {
    constructor() : this("", "", "", "", "", false, "")
}
