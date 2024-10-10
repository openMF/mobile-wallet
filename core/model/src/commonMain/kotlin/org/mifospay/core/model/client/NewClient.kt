/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.client

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Parcelize
data class NewClient(
    val firstname: String,
    val lastname: String,
    val externalId: String,
    val mobileNo: String,
    val address: ClientAddress,
    val savingsProductId: Int,
) : Parcelable
