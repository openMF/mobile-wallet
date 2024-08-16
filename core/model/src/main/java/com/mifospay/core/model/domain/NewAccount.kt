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

import java.util.Date
data class NewAccount(
    var clientId: Int,
    var productId: String? = null,
    var submittedOnDate: Date? = null,
    var accountNo: String,
    var locale: String? = null,
    var dateFormat: String? = null,
)
