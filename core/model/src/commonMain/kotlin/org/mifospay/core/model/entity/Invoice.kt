/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Invoice(
    val consumerId: String? = null,
    val consumerName: String? = null,
    val amount: Double = 0.0,
    val itemsBought: String? = null,
    val status: Long = 0L,
    val transactionId: String? = null,
    val id: Long = 0L,
    val title: String? = null,
    val date: List<Int> = ArrayList(),

    ) {
    constructor() : this(
        consumerId = null,
        consumerName = null,
        amount = 0.0,
        itemsBought = null,
        status = 0L,
        transactionId = null,
        id = 0L,
        title = null,
        date = ArrayList(),
    )
}
