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
import org.mifospay.core.model.domain.client.Client

@Serializable
data class TransferDetail(
    val id: Long = 0L,
    val fromClient: Client = Client(),
    val fromAccount: SavingAccount = SavingAccount(),
    val toClient: Client = Client(),
    val toAccount: SavingAccount = SavingAccount(),
) {
    constructor() : this(
        id = 0L,
        fromClient = Client(),
        fromAccount = SavingAccount(),
        toClient = Client(),
        toAccount = SavingAccount(),
    )
}
