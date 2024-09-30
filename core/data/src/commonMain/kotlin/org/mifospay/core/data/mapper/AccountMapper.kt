/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.mapper

import org.mifospay.core.model.domain.Account
import org.mifospay.core.model.entity.client.ClientAccounts

fun ClientAccounts.toAccount(): List<Account> {
    return this.savingsAccounts.map {
        Account(
            name = it.productName,
            number = it.accountNo,
            id = it.id,
            balance = it.accountBalance,
            currency = it.currency.toModel(),
            productId = it.productId.toLong(),
        )
    }
}
