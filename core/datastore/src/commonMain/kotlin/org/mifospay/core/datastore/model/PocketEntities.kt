/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class PocketAccountEntity(
    val pocketId: Long,
    val id: Long,
    val accountId: Long,
    val accountTypeStr: String,
    val accountNumber: String,
)

@Serializable
data class DetailedPocketAccountEntity(
    val pocket: PocketAccountEntity,
    val productName: String? = null,
    val balance: Double? = null,
    val currencyCode: String? = null,
    val currencyDisplaySymbol: String? = null,
    val decimalPlaces: Int? = null,
    val statusStr: String? = null,
)

@Serializable
data class LinkableAccountEntity(
    val accountId: Long,
    val productName: String? = null,
    val accountNumber: String? = null,
    val accountTypeStr: String,
    val balance: Double? = null,
    val currencyCode: String? = null,
    val currencyDisplaySymbol: String? = null,
    val decimalPlaces: Int? = null,
    val statusStr: String? = null,
)
