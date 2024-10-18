/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.bank

import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize

@Parcelize
data class BankAccountDetails(
    val accountNo: String,
    val bankName: String?,
    val accountHolderName: String?,
    val branch: String?,
    val ifsc: String?,
    val type: String?,
    val isUpiEnabled: Boolean = false,
    val upiPin: String?,
) : Parcelable
