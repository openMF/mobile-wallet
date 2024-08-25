/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.common

import java.text.NumberFormat
import java.util.Currency

object Utils {

    @JvmStatic
    fun getFormattedAccountBalance(
        balance: Double?,
        currencyCode: String?,
        maximumFractionDigits: Int? = 0,
    ): String {
        val accountBalanceFormatter = NumberFormat.getCurrencyInstance()
        accountBalanceFormatter.maximumFractionDigits = maximumFractionDigits ?: 0
        accountBalanceFormatter.currency = Currency.getInstance(currencyCode)
        return accountBalanceFormatter.format(balance)
    }

    fun <T> List<T>.toArrayList(): ArrayList<T> {
        val array: ArrayList<T> = ArrayList()
        for (index in this) array.add(index)
        return array
    }
}
