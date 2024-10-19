/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.model.entity.standinginstruction

import kotlinx.serialization.Serializable
import org.mifospay.core.model.savingsaccount.SavingAccountEntity
import org.mifospay.core.network.model.entity.client.ClientEntity
import org.mifospay.core.network.model.entity.client.Status

@Serializable
data class StandingInstruction(
    val id: Long,
    val name: String,
    val fromClient: ClientEntity,
    val fromAccount: SavingAccountEntity,
    val toClient: ClientEntity,
    val toAccount: SavingAccountEntity,
    val status: Status,
    val amount: Double,
    val validFrom: List<Int>,
    val validTill: List<Int>?,
    val recurrenceInterval: Int,
    val recurrenceOnMonthDay: List<Int>,
)
