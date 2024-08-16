/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.fineract.entity.mapper

import com.mifospay.core.model.domain.Account
import com.mifospay.core.model.entity.client.ClientAccounts
import javax.inject.Inject

class AccountMapper @Inject constructor(
    private val currencyMapper: CurrencyMapper,
) {

    fun transform(clientAccounts: ClientAccounts?): List<Account> {
        val accountList = mutableListOf<Account>()

        clientAccounts?.savingsAccounts?.forEach { savingAccount ->
            val account = Account(
                name = savingAccount.productName,
                number = savingAccount.accountNo,
                id = savingAccount.id,
                balance = savingAccount.accountBalance,
                currency = currencyMapper.transform(savingAccount.currency),
                productId = savingAccount.productId.toLong(),
            )
            accountList.add(account)
        }
        return accountList
    }
}
