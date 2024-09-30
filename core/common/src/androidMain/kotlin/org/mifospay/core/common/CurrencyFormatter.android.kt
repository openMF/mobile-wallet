/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.common

import java.text.NumberFormat
import java.util.Currency

actual object CurrencyFormatter {
    actual fun format(
        balance: Double?,
        currencyCode: String?,
        maximumFractionDigits: Int?,
    ): String {
        val balanceFormatter = NumberFormat.getCurrencyInstance()
        balanceFormatter.maximumFractionDigits = maximumFractionDigits ?: 0
        balanceFormatter.currency = Currency.getInstance(currencyCode)
        return balanceFormatter.format(balance)
    }
}
