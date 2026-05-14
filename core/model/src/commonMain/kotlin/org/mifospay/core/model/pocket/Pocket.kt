/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.pocket

import kotlinx.serialization.Serializable

@Serializable
data class Pocket(
    val id: Long,
    val clientId: Long,
    val clientName: String,
    val linkedAccounts: List<PocketAccount> = emptyList(),
) {
    val totalBalance: Double
        get() = linkedAccounts.sumOf { it.balance }
}

@Serializable
data class PocketAccount(
    val accountId: Long,
    val accountNumber: String,
    val accountType: PocketAccountType,
    val balance: Double,
    val currency: String,
    val productName: String,
    val status: String,
)

enum class PocketAccountType {
    SAVINGS,
    LOAN,
    SHARE,
}

@Serializable
data class PocketLinkPayload(
    val accountId: Long,
    val accountType: String,
)

@Serializable
data class PocketDelinkPayload(
    val accountId: Long,
)
