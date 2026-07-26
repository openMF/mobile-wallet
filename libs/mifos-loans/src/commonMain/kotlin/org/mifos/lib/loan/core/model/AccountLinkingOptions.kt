/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountLinkingOptions(

    val accountNo: String? = null,

    val clientId: Int? = null,

    val clientName: String? = null,

    val currency: Currency? = null,

    val fieldOfficerId: Int? = null,

    val id: Int? = null,

    val productId: Int? = null,

    val productName: String? = null,
)
