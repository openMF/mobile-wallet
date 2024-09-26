/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity.accounts.savings

import kotlinx.serialization.Serializable

@Serializable
data class PaymentDetailData(
    val id: Int? = null,
    val paymentType: PaymentType? = null,
    val accountNumber: String? = null,
    val checkNumber: String? = null,
    val routingCode: String? = null,
    val receiptNumber: String? = null,
    val bankNumber: String? = null,
) {
    constructor() : this(
        id = null,
        paymentType = null,
        accountNumber = null,
        checkNumber = null,
        routingCode = null,
        receiptNumber = null,
        bankNumber = null,
    )
}
